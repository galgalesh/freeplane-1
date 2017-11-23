package org.freeplane.core.ui.menubuilders.generic;

public class SubtreeProcessor implements EntryPopupListener {
	public SubtreeProcessor() {
		super();
	}

	private PhaseProcessor processor;

	public void setProcessor(PhaseProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void childEntriesWillBecomeVisible(Entry entry) {
		if (RecursiveMenuStructureProcessor.shouldProcessOnEvent(entry)) {
			buildChildren(entry);
		}
	}

	public void buildChildren(Entry entry) {
		final PhaseProcessor subtreeProcessor = forChildren(entry);
		subtreeProcessor.buildChildren(entry);
	}

	public void rebuildEntry(Entry entry) {
		final Entry parent = entry.getParent();
		final PhaseProcessor subtreeProcessor = parent != null ? forChildren(parent) : processor;
		subtreeProcessor.destroy(entry);
		subtreeProcessor.build(entry);
	}

	private PhaseProcessor forChildren(Entry entry) {
	    final Entry root = entry.getRoot();
		final PhaseProcessor subtreeProcessor = processor.forChildren(root, entry);
	    return subtreeProcessor;
    }

	@Override
	public void childEntriesHidden(Entry entry) {
		if (RecursiveMenuStructureProcessor.shouldProcessOnEvent(entry)) {
			destroyChildren(entry);
		}
	}

	public void destroyChildren(Entry entry) {
		final PhaseProcessor subtreeProcessor = forChildren(entry);
		for (Entry child : entry.children()) {
			subtreeProcessor.destroy(child);
		}
	}
	
	public void rebuildChildren(Entry entry){
		destroyChildren(entry);
		buildChildren(entry);
	}
}