package org.example.server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.example.core.AdBlockResolver;
import org.example.core.DnsRecord;
import org.xbill.DNS.Message;
import org.xbill.DNS.Type;
import org.xbill.DNS.Section;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.AAAARecord;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.Base64;
import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class DohServer {
    private final AdBlockResolver resolver;

    public DohServer(AdBlockResolver resolver) {
        this.resolver = resolver;
    }

    public void start(int port) {

        Javalin app = Javalin.create(config -> {
            config.concurrency.useVirtualThreads = true;
            config.routes.apiBuilder(() -> {
                get("/dns-query", ctx -> handleDohRequest(ctx, false));
                post("/dns-query", ctx -> handleDohRequest(ctx, true));
            });
        });

        app.start(port);
    }

    void handleDohRequest(Context ctx, boolean isPost) {
        if (isPost && !"application/dns-message".equals(ctx.contentType())) {
            ctx.status(415).result("Unsupported Media Type");
            return;
        }

        try {
            byte[] requestBytes;

            if (isPost) {
                requestBytes = ctx.bodyAsBytes();
            } else {
                String dnsParam = ctx.queryParam("dns");
                if (dnsParam == null) {
                    ctx.status(400).result("Missing 'dns' parameter");
                    return;
                }
                requestBytes = Base64.getUrlDecoder().decode(dnsParam.trim());
            }

            Message dnsQuery = new Message(requestBytes);

            org.xbill.DNS.Record question = dnsQuery.getQuestion();
            if (question == null) {
                ctx.status(400).result("Invalid DNS Query");
                return;
            }

            String domain = question.getName().toString(true);
            int queryType = question.getType();
            DnsRecord.RecordType myType = (queryType == Type.AAAA) ? DnsRecord.RecordType.AAAA : DnsRecord.RecordType.A;

            List<DnsRecord> resolvedIps = resolver.resolveDomain(domain, myType);

            Message dnsResponse = dnsQuery.clone();
            dnsResponse.removeAllRecords(Section.ANSWER);
            dnsResponse.removeAllRecords(Section.AUTHORITY);
            dnsResponse.removeAllRecords(Section.ADDITIONAL);

            dnsResponse.getHeader().setFlag(Flags.QR);

            if (resolvedIps == null || resolvedIps.isEmpty()) {
                dnsResponse.getHeader().setRcode(Rcode.NXDOMAIN);

            } else {
                dnsResponse.getHeader().setRcode(Rcode.NOERROR);
                for (DnsRecord record : resolvedIps) {
                    org.xbill.DNS.Record answerRecord = null;

                    if (record.type() == DnsRecord.RecordType.A && record.ipAddress() instanceof Inet4Address) {
                        answerRecord = new ARecord(question.getName(), DClass.IN, record.ttl(), record.ipAddress());
                    } else if (record.type() == DnsRecord.RecordType.AAAA && record.ipAddress() instanceof Inet6Address) {
                        answerRecord = new AAAARecord(question.getName(), DClass.IN, record.ttl(), record.ipAddress());
                    }

                    if (answerRecord != null) {
                        dnsResponse.addRecord(answerRecord, Section.ANSWER);
                    }
                }
            }

            ctx.contentType("application/dns-message");
            ctx.result(dnsResponse.toWire());

        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid Base64 Encoding");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Internal Server Error");
        }
    }
}
