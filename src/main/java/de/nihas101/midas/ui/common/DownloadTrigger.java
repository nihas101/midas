package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

public class DownloadTrigger implements Serializable {
    private final HasComponents content;

    public DownloadTrigger(final HasComponents content) {
        this.content = content;
    }

    public void triggerDownload(
            final byte[] data,
            final String fileName,
            final String mimeType
    ) {
        final Anchor downloadAnchor = new Anchor(
                DownloadHandler.fromInputStream(event -> new DownloadResponse(
                        new ByteArrayInputStream(data),
                        fileName,
                        mimeType,
                        data.length
                )),
                ""
        );
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.getElement().getStyle().set("display", "none");
        content.add(downloadAnchor);
        downloadAnchor.getElement().executeJs("this.click();");
    }
}