package competition.web.common;


import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

import java.util.Locale;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 15-May-2009
// Time: 10:33:55

//
public class ByteArrayResourceStream implements IResourceStream {

    private Locale locale = null;
    private byte[] content = null;
    private String contentType = null;

    public ByteArrayResourceStream(byte[] content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public void close() throws IOException {
    }

    public String getContentType() {
        return (contentType);
    }

    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        return (new ByteArrayInputStream(content));
    }

    public Locale getLocale() {
        return (locale);
    }

    public Bytes length() {
        return Bytes.bytes(content.length);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Time lastModifiedTime() {
        return null;
    }

	@Override
	public String getStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStyle(String style) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getVariation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVariation(String variation) {
		// TODO Auto-generated method stub
		
	}
}