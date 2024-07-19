package com.tyron.code.desktop.services.navigation;

import com.tyron.code.desktop.ui.docking.DockingManager;
import com.tyron.code.info.ClassInfo;
import com.tyron.code.path.PathNode;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Outline of navigable content. UI components implement this type can be discovered via path-based look-ups.
 *
 * @author Matt Coley
 * @see NavigationManager Tracker of all open {@link Navigable} content.
 */
public interface Navigable {

    /**
     * @return The path layout pointing to the content <i>(Such as a {@link ClassInfo}, {@link ClassMember}, etc)</i>
     * that this {@link Navigable} class is representing.
     */
    @NotNull
    PathNode<?> getPath();

    /**
     * @return Child navigable nodes.
     */
    @NotNull
    Collection<Navigable> getNavigableChildren();

    /**
     * Requests focus of this navigable component.
     */
    void requestFocus();

    /**
     * Disables this navigable component.
     * <p>
     * Called when:
     * <ul>
     *     <li>A {@link Dependent} {@link Navigable} content within a tab tracked by {@link DockingManager} is closed.</li>
     *     <li>An associated {@link ClassInfo} or {@link FileInfo} in the workspace is removed.</li>
     * </ul>
     */
    void disable();

    /**
     * Searches for {@link Navigable} child components in the component model.
     * The model can be thought of as a tree, where child nodes are represented by {@link #getNavigableChildren()}.
     * <br>
     * For UI navigable components, this typically would be implemented by filtering the UI children list
     * <i>(Such as {@link Pane#getChildren()})</i> that implement {@link Navigable}.
     * <br>
     * However, the children are not limited to strictly components that exist akin to {@link Pane#getChildren()}.
     * a UI can wrap a {@link ClassInfo} but then declare its members as {@link Navigable} children. Then each child
     * can implement {@link #requestFocus()} in such a way that is handled in the parent representation of the
     * {@link ClassInfo}.
     *
     * @param path
     * 		Path associated with node to look for in the component model.
     *
     * @return {@link Navigable} components matching the path in the component model.
     */
    @NotNull
    default List<Navigable> getNavigableChildrenByPath(@NotNull PathNode<?> path) {
        PathNode<?> value = getPath();
        if (path.equals(value)) {
            return Collections.singletonList(this);
        }

        List<Navigable> list = null;
        for (Navigable child : getNavigableChildren())
            if (path.isDescendantOf(child.getPath())) {
                List<Navigable> childM = child.getNavigableChildrenByPath(path);
                if (!childM.isEmpty()) {
                    if (list == null) {
                        list = new ArrayList<>(childM);
                    } else
                        list.addAll(childM);
                }
            }

        return list == null ? Collections.emptyList() : list;
    }
}
