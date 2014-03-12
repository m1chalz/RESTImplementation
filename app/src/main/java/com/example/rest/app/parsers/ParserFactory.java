package com.example.rest.app.parsers;

import android.net.Uri;
import com.example.rest.app.provider.AppContract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParserFactory {

	private ParserFactory() {}

	@Nullable
	public static Parser GetParserByUriString(@NotNull String uriStr) {
		Uri uri = Uri.parse(uriStr);
		return GetParserByUri(uri);
	}

	@Nullable
	public static Parser GetParserByUri(@NotNull Uri uri) {

		if (uri.compareTo(AppContract.Entities.CONTENT_URI) == 0) {
			return new EntitiesParser();
		}

		return null;
	}
}
