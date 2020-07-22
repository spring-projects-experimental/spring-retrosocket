package com.joshlong.rsocket.client.metadata;

import org.springframework.util.MimeType;

class Constants {

	public static final String CLIENT_ID_HEADER = "client-id";

	public static final String LANG_HEADER = "lang";

	public static final String CLIENT_ID_MIME_TYPE_VALUE = "messaging/x.bootiful." + CLIENT_ID_HEADER;

	public static final String LANG_MIME_TYPE_VALUE = "messaging/x.bootiful." + LANG_HEADER;

	public static final MimeType CLIENT_ID_MIME_TYPE = MimeType.valueOf(CLIENT_ID_MIME_TYPE_VALUE);

	public static final MimeType LANG_MIME_TYPE = MimeType.valueOf(LANG_MIME_TYPE_VALUE);

}
