package com.example.blog.common.logging;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StreamUtils;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
  private final byte[] cachedBody;

  public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
    super(request);

    // Content-Type이 form이거나 body가 없는 경우 빈 배열로 처리
    String contentType = request.getContentType();
    if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
      this.cachedBody = new byte[0]; // 폼 데이터는 캐시하지 않음
    } else {
      InputStream requestInputStream = request.getInputStream();
      this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
    }
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return new CachedBodyServletInputStream(this.cachedBody);
  }

  @Override
  public BufferedReader getReader() throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
    return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
  }

  public byte[] getCachedBody() {
    return this.cachedBody.clone(); // 방어적 복사
  }

  private static class CachedBodyServletInputStream extends ServletInputStream {
    private final InputStream cachedBodyInputStream;
    private boolean finished = false;

    public CachedBodyServletInputStream(byte[] cachedBody) {
      this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public boolean isFinished() {
      return finished;
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int read() throws IOException {
      int data = cachedBodyInputStream.read();
      if (data == -1) {
        finished = true;
      }
      return data;
    }
  }
}
