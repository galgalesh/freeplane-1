package org.freeplane.plugin.script;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.freeplane.core.util.HtmlUtils;
import org.freeplane.core.util.LogUtils;
import org.freeplane.features.map.NodeModel;

public class ScriptContext {
	private static final class NodeWrapper {
		private final NodeModel nodeModel;
		private final String script;

		public NodeWrapper(NodeModel nodeModel, String script) {
			this.nodeModel = nodeModel;
			// NOTE: to ignore the script for cycle detection comment out next line
			this.script = script;
		}

		public NodeModel getNodeModel() {
			return nodeModel;
		}

		//		public String getScript() {
		//			return script;
		//		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((nodeModel == null) ? 0 : nodeModel.hashCode());
			result = prime * result + ((script == null) ? 0 : script.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NodeWrapper other = (NodeWrapper) obj;
			if (nodeModel != other.nodeModel)
				return false;
			if (script == null) {
				if (other.script != null)
					return false;
			}
			return script.equals(other.script);
		}

		@Override
		public String toString() {
			return nodeModel + "[" + script + "]";
		}
	}

	private final UniqueStack<NodeWrapper> stack = new UniqueStack<NodeWrapper>();

	public ScriptContext() {
	}

    public void accessNode(final NodeModel accessedNode) {
        final NodeWrapper nodeWrapper = stackLastLogNull("accessNode");
        if (nodeWrapper != null)
            FormulaUtils.accessNode(nodeWrapper.getNodeModel(), accessedNode);
    }

    public void accessBranch(final NodeModel accessedNode) {
        final NodeWrapper nodeWrapper = stackLastLogNull("accessBranch");
        if (nodeWrapper != null)
            FormulaUtils.accessBranch(nodeWrapper.getNodeModel(), accessedNode);
    }

	public void accessAll() {
		final NodeWrapper nodeWrapper = stackLastLogNull("accessAll");
		if (nodeWrapper != null)
		    FormulaUtils.accessAll(nodeWrapper.getNodeModel());
	}

    @SuppressWarnings("unused")
    private NodeWrapper stackLastLogNull(String method) {
        final NodeWrapper last = stack.last();
        if (FormulaUtils.DEBUG_FORMULA_EVALUATION && last == null)
            System.err.println("stack is empty on " + method);
        return last;
    }

	public boolean push(NodeModel nodeModel, String script) {
		final boolean success = stack.push(new NodeWrapper(nodeModel, script));
		if (!success) {
			LogUtils.warn("Circular reference detected! Traceback (innermost last):\n " //
			        + stackTrace(nodeModel, script));
		}
		return success;
	}

	public void pop() {
		stack.pop();
	}

	public NodeModel getStackFront() {
		return stack.first().getNodeModel();
	}
	
	public String stackTrace(NodeModel nodeModel, String script) {
		ArrayList<String> entries = new ArrayList<String>(stack.size());
		for (NodeWrapper node : stack) {
			entries.add(format(node.nodeModel, node.script, nodeModel));
		}
		entries.add(format(nodeModel, script, nodeModel));
		return StringUtils.join(entries.iterator(), "\n -> ");
	}

	private String format(NodeModel nodeModel, String script, NodeModel nodeToHighlight) {
		return (nodeToHighlight.equals(nodeModel) ? "* " : "") + nodeModel.createID() + " "
		        + limitLength(deformat(nodeModel.getText()), 30) //
		        + " -> " + limitLength(script, 60);
	}

	private String deformat(String string) {
		return HtmlUtils.htmlToPlain(string).replaceAll("\\s+", " ");
	}

	private String limitLength(final String string, int maxLenght) {
		if (string == null || maxLenght >= string.length())
			return string;
		maxLenght = maxLenght > 3 ? maxLenght - 3 : maxLenght;
		return string.substring(0, maxLenght) + "...";
	}

	@Override
	public String toString() {
		return stack.toString();
	}
}
