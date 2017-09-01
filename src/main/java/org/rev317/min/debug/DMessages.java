package org.rev317.min.debug;

import org.parabot.core.Context;
import org.parabot.core.paint.AbstractDebugger;
import org.parabot.core.paint.PaintDebugger;
import org.rev317.min.api.events.MessageEvent;
import org.rev317.min.api.events.listeners.MessageListener;
import org.rev317.min.script.ScriptEngine;

import java.awt.*;

/**
 * @author JKetelaar
 */
public class DMessages extends AbstractDebugger implements MessageListener {
    private boolean enabled;

    private String message;
    private String sender;
    private String title;
    private int    type;

    @Override
    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            ScriptEngine.getInstance().addMessageListener(this);
        } else {
            ScriptEngine.getInstance().removeMessageListener(this);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void paint(Graphics graphics) {
        PaintDebugger p = Context.getInstance().getPaintDebugger();
        p.addLine("Message: " + message);
        p.addLine("Sender: " + sender);
        p.addLine("Title: " + title);
        p.addLine("Type: " + type);
    }

    @Override
    public void messageReceived(MessageEvent event) {
        message = event.getMessage();
        sender = event.getSender();
        title = event.getTitle();
        type = event.getType();
        System.out.printf("\"%s\" from \"%s\" with title \"%s\" and type %d\n", message, sender, title, type);
    }
}
