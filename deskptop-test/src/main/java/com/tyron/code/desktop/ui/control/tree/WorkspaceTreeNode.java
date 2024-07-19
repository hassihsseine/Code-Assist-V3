package com.tyron.code.desktop.ui.control.tree;

import com.tyron.code.path.PathNode;

import com.tyron.code.path.impl.DirectoryPathNode;
import com.tyron.code.path.impl.ModulePathNode;
import com.tyron.code.project.model.module.Module;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tree item subtype for more convenience tree building operations.
 *
 * @author Matt Coley
 */
public class WorkspaceTreeNode extends FilterableTreeItem<PathNode<?>> implements Comparable<WorkspaceTreeNode> {
	/**
	 * Create new node with path value.
	 *
	 * @param path
	 * 		Path of represented item.
	 */
	public WorkspaceTreeNode(PathNode<?> path) {
		setValue(path);
	}

	/**
	 * Removes a tree node from the tree by its {@link PathNode} equality.
	 *
	 * @param path
	 * 		Path to remove from the tree.
	 *
	 * @return {@code true} when removal is a success.
	 * {@code false} if nothing was removed.
	 */
	public boolean removeNodeByPath(@NotNull PathNode<?> path) {
		// Call from root node only.
		WorkspaceTreeNode root = this;
		while (root.getParent() instanceof WorkspaceTreeNode parentNode)
			root = parentNode;

		// Get node by path.
		WorkspaceTreeNode nodeByPath = root.getNodeByPath(path);

		// Get that node's parent, remove the child.
		if (nodeByPath != null && nodeByPath.getParent() instanceof WorkspaceTreeNode parentNode) {
			boolean removed = parentNode.removeSourceChild(nodeByPath);
			while (parentNode.isLeaf() && parentNode.getParentNode() != null) {
				WorkspaceTreeNode parentOfParent = parentNode.getParentNode();
				parentOfParent.removeSourceChild(parentNode);
				parentNode = parentOfParent;
			}
			return removed;
		}

		// No known node by path.
		return false;
	}

	/**
	 * Gets or creates a tree node by the given {@link PathNode}.
	 *
	 * @param path
	 * 		Path associated with node to look for in tree.
	 *
	 * @return Node containing the path in the tree.
	 */
	@NotNull
	public WorkspaceTreeNode getOrCreateNodeByPath(@NotNull PathNode<?> path) {
		// Call from root node only.
		WorkspaceTreeNode root = this;
		while (root.getParent() instanceof WorkspaceTreeNode parentNode)
			root = parentNode;

		// Lookup and/or create nodes for path.
		return getOrInsertIntoTree(root, path);
	}

	/**
	 * Searches for a {@link WorkspaceTreeNode} item in the tree model, matching the given path.
	 *
	 * @param path
	 * 		Path associated with node to look for in tree.
	 *
	 * @return Node containing the path in the tree.
	 */
	@Nullable
	@SuppressWarnings("deprecation")
	public WorkspaceTreeNode getNodeByPath(@NotNull PathNode<?> path) {
		PathNode<?> value = getValue();
		if (path.equals(value)) {
            return this;
        }

		for (TreeItem<PathNode<?>> child : getChildren()) {
            if (path.isDescendantOf(child.getValue()) && child instanceof WorkspaceTreeNode childNode) {
                return childNode.getNodeByPath(path);
            }
        }

		return null;
	}

	/**
	 * @param path
	 * 		Path to check against.
	 *
	 * @return {@code true} when the current node's path matches.
	 */
	public boolean matches(@NotNull PathNode<?> path) {
		return path.equals(getValue());
	}

	/**
	 * @return {@link #getParent()} but cast to {@link WorkspaceTreeNode}.
	 */
	@Nullable
	public WorkspaceTreeNode getParentNode() {
		return (WorkspaceTreeNode) getParent();
	}

	@Override
	public int compareTo(@NotNull WorkspaceTreeNode o) {
		return getValue().compareTo(o.getValue());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getValue().toString() + "]";
	}


	/**
	 * Get/insert a {@link WorkspaceTreeNode} holding the given {@link PathNode} from/to the tree model.
	 *
	 * @param node
	 * 		Tree node to insert into.
	 * @param path
	 * 		Path to insert, relative to the given node.
	 *
	 * @return Inserted node.
	 */
	@NotNull
	@SuppressWarnings("deprecation")
	public static WorkspaceTreeNode getOrInsertIntoTree(@NotNull WorkspaceTreeNode node, @NotNull PathNode<?> path) {
		return getOrInsertIntoTree(node, path, false);
	}

	/**
	 * Get/insert a {@link WorkspaceTreeNode} holding the given {@link PathNode} from/to the tree model.
	 *
	 * @param node
	 * 		Tree node to insert into.
	 * @param path
	 * 		Path to insert, relative to the given node.
	 *
	 * @return Inserted node.
	 */
	@NotNull
	@SuppressWarnings("deprecation")
	public static WorkspaceTreeNode getOrInsertIntoTree(@NotNull WorkspaceTreeNode node, @NotNull PathNode<?> path, boolean sorted) {
		// Edge case handling for directory nodes.
		if (path instanceof DirectoryPathNode directoryPath) {
			// If we have parent links in our path, insert those first.
			// We should generate up to whatever context our parent is.
			PathNode<Module> parent = directoryPath.getParent();
			if (parent != null) {
                node = getOrInsertIntoTree(node, parent, sorted);
            }

			// Work off of the first node that does NOT contain a directory value.
			while (node.getValue() instanceof DirectoryPathNode) {
				node = (WorkspaceTreeNode) node.getParent();
			}

			// Insert the directory path, separated by '/'.
			// Update 'node' as we build/fetch the directory path items.
			// We use '-1' as a limit in split to allow empty directories to be split properly:
			//  '//' --> ['', '', '']
			String fullDirectory = directoryPath.getValue();
			String[] directoryParts = fullDirectory.split("/", -1);

			StringBuilder directoryBuilder = new StringBuilder();
			for (String directoryPart : directoryParts) {
				// Build up directory path.
				directoryBuilder.append(directoryPart).append('/');
				String directoryName = directoryBuilder.substring(0, directoryBuilder.length() - 1);
				DirectoryPathNode localPathNode = directoryPath.withDirectory(directoryName);

				// Get existing tree node, or create child if non-existent
				WorkspaceTreeNode childNode = null;
				for (TreeItem<PathNode<?>> child : node.getChildren())
					if (child.getValue().equals(localPathNode)) {
						childNode = (WorkspaceTreeNode) child;
						break;
					}
				if (childNode == null) {
					childNode = new WorkspaceTreeNode(localPathNode);
					if (sorted) {
						node.addPreSortedChild(childNode);
					} else {
						node.addAndSortChild(childNode);
					}
				}

				// Prepare for next directory path entry.
				node = childNode;
			}
			return node;
		}

		// If we have parent links in our path, insert those first.
		// We should generate up to whatever context our parent is.
		PathNode<?> parent = path.getParent();
		if (parent != null) {
            node = getOrInsertIntoTree(node, parent, sorted);
        } else if (path.typeIdMatch(node.getValue())) {
			// We are the root link in the path. This check ensures that as the root type we do not
			// insert a new tree-node of the same value, to the children list of the root tree node.
			return node;
		}

		// Check if already inserted.
		for (TreeItem<PathNode<?>> child : node.getChildren()) {
            if (path.equals(child.getValue())) {
                return (WorkspaceTreeNode) child;
            }
        }

		// Not already inserted, create a new node and insert it.
		WorkspaceTreeNode inserted = new WorkspaceTreeNode(path);
		if (sorted) {
			node.addPreSortedChild(inserted);
		} else {
			node.addAndSortChild(inserted);
		}
		return inserted;
	}
}