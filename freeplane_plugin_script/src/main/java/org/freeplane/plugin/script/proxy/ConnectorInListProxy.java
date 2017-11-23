/**
 * 
 */
package org.freeplane.plugin.script.proxy;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.freeplane.features.link.NodeLinkModel;
import org.freeplane.features.link.MapLinks;
import org.freeplane.features.map.NodeModel;
import org.freeplane.plugin.script.ScriptContext;

class ConnectorInListProxy extends AbstractCollection<Proxy.Connector> {
	private final NodeModel node;
	private final ScriptContext scriptContext;

	public ConnectorInListProxy(final NodeProxy nodeProxy) {
		this.node = nodeProxy.getDelegate();
		this.scriptContext = nodeProxy.getScriptContext();
	}

	List<NodeLinkModel> getConnectorSet() {
		final MapLinks allLinks = MapLinks.getLinks(node.getMap());
        final Set<NodeLinkModel> links = allLinks == null ? null : allLinks.get(node.getID());
		return links == null ? Collections.<NodeLinkModel> emptyList() : Collections
		    .unmodifiableList(new ArrayList<NodeLinkModel>(links));
	}

	@Override
	public Iterator<Proxy.Connector> iterator() {
		return new ConnectorIterator(getConnectorSet().iterator(), scriptContext);
	}

	@Override
	public int size() {
		return getConnectorSet().size();
	}
}
