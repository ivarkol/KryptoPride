package ru.airiva.config.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by Ivan
 *
 * Обертка для ответа. Нужна для возможности изменения ответа, в частности для добавления токена в ответ.
 */
public class CapturingResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream capture;
    private ServletOutputStream output;
    private PrintWriter writer;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the {@link HttpServletResponse} to be wrapped.
     * @throws IllegalArgumentException if the response is null
     */
    public CapturingResponseWrapper(HttpServletResponse response) {
        super(response);
        capture = new ByteArrayOutputStream(response.getBufferSize());
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (writer != null) {
            throw new IllegalStateException("Метод getWriter() уже был вызван для данного запроса");
        }

        if (output == null) {
            output = new ServletOutputStream() {

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                    throw new RuntimeException("setWriteListener not implemented");
                }

                @Override
                public void write(int b) {
                    capture.write(b);
                }

                @Override
                public void flush() throws IOException {
                    capture.flush();
                }

                @Override
                public void close() throws IOException {
                    capture.close();
                }
            };

        }
        return output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (output != null) {
            throw new IllegalStateException("Метод getOutputStream() уже был вызван для этого запроса");
        }
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(capture, getCharacterEncoding()));
        }
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();
        if (writer != null) writer.flush();
        if (output != null) output.flush();
    }

    private byte[] getCaptureAsBytes() throws IOException {
        if (writer != null) {
            writer.close();
        }
        else if (output != null) {
            output.close();
        }

        return capture.toByteArray();
    }

    public String getCaptureAsString() throws IOException {
        return new String(getCaptureAsBytes(), getCharacterEncoding());
    }
}
