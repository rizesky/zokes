package xyz.zes.zokes;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks user activity to determine focus and break periods
 */
@Service
public final class ActivityTracker implements EditorMouseListener, EditorFactoryListener, Disposable {
    private final AtomicLong lastActivityTime = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong lastTypingTime = new AtomicLong(0);
    private final AtomicLong typingStartTime = new AtomicLong(0);
    private volatile boolean isCurrentlyTyping = false;

    private final TypedActionHandler originalTypedHandler;
    private final TypedAction typedAction;

    public ActivityTracker() {
        // Register for editor events
        EditorFactory.getInstance().addEditorFactoryListener(this, ApplicationManager.getApplication());

        // Wrap the typed action handler to detect typing
        typedAction = EditorActionManager.getInstance().getTypedAction();
        originalTypedHandler = typedAction.getHandler();

        typedAction.setupHandler((editor, charTyped, dataContext) -> {
            recordTyping();
            originalTypedHandler.execute(editor, charTyped, dataContext);
        });
    }

    // Record any user activity
    private void recordActivity() {
        lastActivityTime.set(System.currentTimeMillis());
    }

    // Record typing specific activity
    private void recordTyping() {
        long now = System.currentTimeMillis();
        long lastType = lastTypingTime.getAndSet(now);
        recordActivity();

        // If this is the first keystroke after a pause, record the start time
        if (!isCurrentlyTyping) {
            typingStartTime.set(now);
            isCurrentlyTyping = true;
        }

        // If there's a long pause between keystrokes, consider it a new typing session
        if ((now - lastType) > 5000) { // 5 second threshold
            typingStartTime.set(now);
        }
    }

    // EditorMouseListener implementations
    @Override
    public void mouseClicked(@NotNull EditorMouseEvent event) {
        recordActivity();
        isCurrentlyTyping = false; // Mouse click likely interrupts typing flow
    }

    @Override
    public void mousePressed(@NotNull EditorMouseEvent event) {
        recordActivity();
        isCurrentlyTyping = false;
    }

    @Override
    public void mouseReleased(@NotNull EditorMouseEvent event) {
        recordActivity();
    }

    @Override
    public void mouseEntered(@NotNull EditorMouseEvent event) {
        // Not used but must be implemented
    }

    @Override
    public void mouseExited(@NotNull EditorMouseEvent event) {
        // Not used but must be implemented
    }

    // EditorFactoryListener implementations
    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        event.getEditor().addEditorMouseListener(this);
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        event.getEditor().removeEditorMouseListener(this);
    }

    /**
     * Determines if the user appears to be taking a break
     * @param breakThresholdSeconds how many seconds of inactivity to consider a break
     * @return true if the user seems to be on a break
     */
    public boolean isUserTakingBreak(int breakThresholdSeconds) {
        long now = System.currentTimeMillis();
        long inactivityTime = now - lastActivityTime.get();
        return inactivityTime > (breakThresholdSeconds * 1000L);
    }

    /**
     * Determines if the user appears to be in deep focus (continuous typing)
     * @param focusThresholdSeconds how many seconds of continuous typing to consider "focused"
     * @return true if the user seems deeply focused
     */
    public boolean isUserFocused(int focusThresholdSeconds) {
        if (!isCurrentlyTyping) {
            return false;
        }

        long now = System.currentTimeMillis();

        // Check if they've been typing for at least the focus threshold
        long typingDuration = now - typingStartTime.get();
        long timeSinceLastType = now - lastTypingTime.get();

        // User is in a focused state if they've been typing for a while
        // and the last keystroke was recent
        return typingDuration > (focusThresholdSeconds * 1000L) &&
                timeSinceLastType < 3000; // Less than 3 seconds since last keystroke
    }

    @Override
    public void dispose() {
        // Restore the original typed handler
        if (typedAction != null && originalTypedHandler != null) {
            typedAction.setupHandler(originalTypedHandler);
        }

        // No need to manually remove the EditorFactoryListener - it's automatically removed
        // by the disposable mechanism when registered with a parent disposable

        // Remove mouse listeners from all open editors
        for (Editor editor : EditorFactory.getInstance().getAllEditors()) {
            editor.removeEditorMouseListener(this);
        }
    }
}