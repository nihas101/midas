package de.nihas101.midas.desktop;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

@org.springframework.stereotype.Component
public class VaadinDesktopInitListener implements VaadinServiceInitListener {

    private final DesktopLifecycleService desktopLifecycleService;

    public VaadinDesktopInitListener(DesktopLifecycleService desktopLifecycleService) {
        this.desktopLifecycleService = desktopLifecycleService;
    }

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiInitEvent -> {
            UI ui = uiInitEvent.getUI();

            // Add a hidden component to the UI that can receive calls from JS
            CloseHandler handler = new CloseHandler();
            ui.add(handler);

            ui.getElement().executeJs(
                    "window.addEventListener('beforeunload', function() { $0.$server.closeUI(); })",
                    handler.getElement()
            );

            desktopLifecycleService.uiAttached();
            ui.addDetachListener(detachEvent -> desktopLifecycleService.uiDetached());
        });
    }

    /**
     * Helper component to receive the close signal from the browser.
     */
    @Tag("div")
    public static class CloseHandler extends Component {
        @ClientCallable
        public void closeUI() {
            getUI().ifPresent(UI::close);
        }
    }
}
