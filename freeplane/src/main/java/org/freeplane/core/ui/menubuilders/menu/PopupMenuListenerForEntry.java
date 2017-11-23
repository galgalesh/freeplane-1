package org.freeplane.core.ui.menubuilders.menu;

import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.freeplane.core.ui.menubuilders.generic.Entry;
import org.freeplane.core.ui.menubuilders.generic.EntryAccessor;
import org.freeplane.core.ui.menubuilders.generic.EntryPopupListener;

class PopupMenuListenerForEntry implements PopupMenuListener{
	private final Entry entry;
	private final EntryPopupListener popupListener;
	final EntryAccessor entryAccessor = new EntryAccessor();

	PopupMenuListenerForEntry(Entry entry, EntryPopupListener popupListener) {
		this.entry = entry;
		this.popupListener = popupListener;
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		fireChildEntriesWillBecomeVisible(entry);
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fireChildEntriesHidden(entry);
			}
		});
	}
	
	private void fireChildEntriesWillBecomeVisible(final Entry entry) {
		popupListener.childEntriesWillBecomeVisible(entry);
		for (Entry child : entry.children())
			if (!(entryAccessor.getComponent(child) instanceof JMenu))
				fireChildEntriesWillBecomeVisible(child);
	}

	private void fireChildEntriesHidden(final Entry entry) {
	    popupListener.childEntriesHidden(entry);
		for (Entry child : entry.children())
			if (!(entryAccessor.getComponent(child) instanceof JMenu))
				fireChildEntriesHidden(child);
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}
}