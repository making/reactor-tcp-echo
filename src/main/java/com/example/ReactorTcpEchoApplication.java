package com.example;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.ipc.netty.tcp.TcpServer;

public class ReactorTcpEchoApplication {

	private static final Logger log = LoggerFactory
			.getLogger(ReactorTcpEchoApplication.class);

	public static void main(String[] args) {
		int port = Optional.ofNullable(System.getenv("PORT")).map(Integer::valueOf)
				.orElse(8080);
		TcpServer tcpServer = TcpServer.create("0.0.0.0", port);
		log.info("Launching echo server on port {}", port);
		try {
			tcpServer.startAndAwait(ch -> ch.receiveString(StandardCharsets.UTF_8).next()
					.publish(s -> ch.sendString(s, StandardCharsets.UTF_8)));
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				log.info("Shutdown...");
				tcpServer.shutdownAndAwait();
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}));
	}
}
