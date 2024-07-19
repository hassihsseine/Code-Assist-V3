package com.tyron.code.path;

import com.tyron.code.project.Workspace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A <i>"modular"</i> value type for representing <i>"paths"</i> to content in a {@link Workspace}.
 * The path must contain all data in a <i>"chain"</i> such that it can have access from most specific portion
 * all the way up to the {@link Workspace} portion.
 *
 * @param <V> Path value type.
 * @author Matt Coley
 */
public interface PathNode<V> extends Comparable<PathNode<?>> {

    /**
     * The parent node of this node. This value does not have to be present in the actual UI model.
     * The parent linkage is so that child types like {@link ClassPathNode} can access their full scope,
     * including their containing {@link DirectoryPathNode package}, {@link BundlePathNode bundle},
     * {@link ResourcePathNode resource}, and {@link WorkspacePathNode workspace}.
     * <br>
     * This allows child-types such as {@link ClassPathNode} to be passed around to consuming APIs and retain access
     * to the mentioned scoped values.
     *
     * @return Parent node.
     * @see #getValueOfType(Class) Used by child-types to look up values in themselves, and their parents.
     */
    @Nullable
    @SuppressWarnings("rawtypes")
    PathNode getParent();

    /**
     * @param type Some type contained in the full path.
     *             This includes the current {@link PathNode} and any {@link #getParent() parent}.
     * @param <T>  Implied value type.
     * @param <I>  Implied path node implementation type.
     * @return Node in the path holding a value of the given type.
     * @see #getValueOfType(Class) Get the direct value of the parent node.
     */
    @Nullable <T, I extends PathNode<T>> I getParentOfType(@NotNull Class<T> type);

    /**
     * @return Wrapped value.
     */
    @NotNull
    V getValue();

    /**
     * @param other Some other path node.
     * @return {@code true} when the other path has the same {@link #getValue() local value}.
     */
    default boolean hasEqualOrChildValue(@NotNull PathNode<?> other) {
        return this == other || getValue().equals(other.getValue());
    }

    /**
     * Used to differentiate path nodes in a chain that have the same {@link #getValueType()}.
     *
     * @return String unique ID per path-node type.
     */
    @NotNull
    String typeId();

    /**
     * @param node Other node to check.
     * @return {@code true} when the current {@link #typeId()} is the same as the other's ID.
     */
    default boolean typeIdMatch(@NotNull PathNode<?> node) {
        return typeId().equals(node.typeId());
    }

    /**
     * @return Set of expected {@link #typeId()} values for {@link #getParent() parent nodes}.
     */
    @NotNull
    Set<String> directParentTypeIds();

    /**
     * @return The type of this path node's {@link #getValue() wrapped value}.
     */
    @NotNull
    Class<V> getValueType();

    /**
     * @param type Some type contained in the full path.
     *             This includes the current {@link PathNode} and any {@link #getParent() parent}.
     * @param <T>  Implied value type.
     * @return Instance of value from the path, or {@code null} if not found in this path.
     * @see #getParentOfType(Class) Get the containing {@link PathNode} instead of the direct value.
     */
    @Nullable <T> T getValueOfType(@NotNull Class<T> type);

    /**
     * Checks for tree alignment. Consider this simple example:
     * <pre>
     *   Path1   Path2   Path3
     *     A       A       A
     *     |       |       |
     *     B       B       B
     *     |       |       |
     *     C       C       X
     * </pre>
     * With this setup:
     * <ul>
     *     <li>{@code path1C.allParentsMatch(path1C) == true} Self checks are equal</li>
     *     <li>{@code path1C.allParentsMatch(path2C) == true} Two identical paths <i>(by value of each node)</i> are equal</li>
     *     <li>{@code path1C.allParentsMatch(path2B) == false} Comparing between non-parallel levels are not equal</li>
     *     <li>{@code path1C.allParentsMatch(path3X) == false} Paths to different items are not equal</li>
     * </ul>
     *
     * @param other Some other path node.
     * @return {@code true} when from this level all parents going up the path match values.
     */
    default boolean allParentsMatch(@NotNull PathNode<?> other) {
        // Type identifiers should match for all levels.
        if (!typeId().equals(other.typeId())) {
            return false;
        }

        // Should both have the same level of tree heights (number of parents).
        PathNode<?> parent = getParent();
        PathNode<?> otherParent = other.getParent();
        if (parent == null && otherParent == null) {
            // Root node edge case
            return hasEqualOrChildValue(other);
        } else if (parent == null || otherParent == null) {
            // Mismatch in tree structure height
            return false;
        }

        // Go up the chain if the matching values continue.
        if (hasEqualOrChildValue(other)) {
            return parent.allParentsMatch(otherParent);
        }
        return false;
    }

    /**
     * @param other
     * 		Some other path node.
     *
     * @return {@code true} when our path represents a more generic path than the given one.
     * {@code false} when our path does not belong to parent path of the given item.
     */
    default boolean isParentOf(@NotNull PathNode<?> other) {
        return other.isDescendantOf(this);
    }

    /**
     * @param other
     * 		Some other path node.
     *
     * @return {@code true} when our path represents a more specific path than the given one.
     * {@code false} when our path does not belong to a potential sub-path of the given item.
     */
    default boolean isDescendantOf(@NotNull PathNode<?> other) {
        // If our type identifiers are the same everything going up the path should match.
        String otherTypeId = other.typeId();
        if (otherTypeId.equals(typeId())) {
            return hasEqualOrChildValue(other) && allParentsMatch(other);
        }

        // Check if the other is an allowed parent.
        PathNode<?> parent = getParent();
        if (directParentTypeIds().contains(otherTypeId) && parent != null) {
            // The parent is an allowed type, check if the parent says it is a descendant of the other path.
            if (parent == other || (parent.hasEqualOrChildValue(other))) {
                return parent.isDescendantOf(other);
            }
        }

        // Check in parent.
        if (parent != null) {
            return parent.isDescendantOf(other);
        }

        // Not a descendant.
        return false;
    }

    /**
     * @param o
     * 		Some other path node.
     *
     * @return Comparison for visual sorting purposes.
     */
    int localCompare(PathNode<?> o);
}
