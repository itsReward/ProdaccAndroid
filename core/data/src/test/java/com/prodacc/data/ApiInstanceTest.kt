package com.prodacc.data

import junit.framework.TestCase.assertEquals
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class ApiInstanceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: OkHttpClient
    private lateinit var webSocket: WebSocket

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        client = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testWebSocketConnection() {
        val request = Request.Builder()
            .url(mockWebServer.url("/websocket"))
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                assertEquals(101, response.code)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                assertEquals("Hello, WebSocket!", text)
            }
        }

        webSocket = client.newWebSocket(request, listener)

        mockWebServer.enqueue(MockResponse().withWebSocketUpgrade(listener))

        webSocket.send("Hello, WebSocket!")
    }


}