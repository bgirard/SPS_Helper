package org.mozilla.profiler.sps.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.mozilla.profiler.sps.Marker;
import org.mozilla.profiler.sps.Sample;
import org.mozilla.profiler.sps.SampleFilter;
import org.mozilla.profiler.sps.SampleLog;

public class ViewList extends View {

	private JTree tree;
	private DefaultMutableTreeNode root;
	private Object selectionStart = null;
	private Object selectionEnd = null;
	private BorderLayout viewLayoutManager;
	private JList markerLists;
	private DefaultListModel markerListModel;
	private DefaultTreeModel treeModel;
	private List<String> highlightMatchingStack;

	public ViewList(SampleLog log) {
		super(log);
		
		viewLayoutManager = new BorderLayout();
		setLayout(viewLayoutManager);

        //Create the nodes.
        
		JPanel southPanel = getSouthPanel();
		
		update();
		add(southPanel, BorderLayout.SOUTH);
	}
	
	private JPanel getSouthPanel() {
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		
		markerListModel = new DefaultListModel();
		final JList jList = new JList(markerListModel);
		jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jList.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				Object[] selection = jList.getSelectedValues();
				if( selection == null || selection.length < 2 ) {
					selectionStart = null;
					selectionEnd = null;
				} else {
					selectionStart = selection[0];
					selectionEnd = selection[selection.length-1];
				}
				jList.removeListSelectionListener(this);
				update();
				jList.addListSelectionListener(this);
			}
		});
		jList.setVisibleRowCount(8);
		JScrollPane jScrollPane = new JScrollPane(new TimeLine(getSampleLog()));
		south.add(jScrollPane, BorderLayout.CENTER);
		south.add(jList, BorderLayout.EAST);
		return south;
	}
	
	private void update() {

		if( tree != null )
			remove(tree);
		
		root = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);
		tree.addTreeSelectionListener( new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent e) {
				Object[] path = tree.getSelectionModel().getSelectionPath().getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path[path.length-1];
				if( node.getUserObject() instanceof String ) {
					highlightMatchingStack = null;
					ViewList.this.repaint();
					return;
				}
				StackNode n = (StackNode)node.getUserObject();
				highlightMatchingStack  = n.stack;
				
				ViewList.this.repaint();
			}
		});
		
		populateView(root);

		add(tree, BorderLayout.CENTER);
		validate();
		repaint();
	}
	private class StackNode {
		private final String name;
		private final SampleLog log;
		private int c = 1;
		private final List<String> stack;
		public StackNode(SampleLog log, List<String> stack, String name) {
			this.log = log;
			this.stack = stack;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public List<String> getStack() {
			return stack;
		}
		public void addCount() {
			c++;
		}
		@Override
		public String toString() {
			return String.format("%6.2f %s", c * 100.0 / log.samples.size(), getName());
		}
	}

	private void populateView(DefaultMutableTreeNode root) {
		//root.removeAllChildren();
		//while (root.getChildCount() > 0) {
			//treeModel.removeNodeFromParent((MutableTreeNode)root.getChildAt(0));
		//}
		SampleLog filter = SampleFilter.filter(log, selectionStart, selectionEnd);
		List<Sample> samples = filter.samples;
		System.out.println("Selected samples: " + samples.size());
		for (Sample sample : samples) {
			
			List<Marker> markers = sample.getMarkers();
			for (int i = 0; i < markers.size() && i < 6; i++) {
				markerListModel.addElement(markers.get(i));
			}
			
			DefaultMutableTreeNode currNode = root;
			List<String> stack = sample.getStack();
			for (String stackName : stack) {
				StackNode currStackNode = null;
				// Find the stack in the current node, if its not there insert it
				Enumeration children = currNode.children();
				DefaultMutableTreeNode node;
				while (children.hasMoreElements()) {
					node = (DefaultMutableTreeNode) children.nextElement();
					StackNode childNode = (StackNode) node.getUserObject();
					if( childNode.getName().equals(stackName) ) {
						currStackNode = childNode;
						currNode = node;
						childNode.addCount();
						break;
					}
				}
				if( currStackNode == null ) {
					currStackNode = new StackNode(log, stack, stackName);
					node = new DefaultMutableTreeNode(currStackNode);
					currNode.add(node);
					currNode = node;
				}
			}
			
		}
		
	}

    private class BookInfo {
        public String bookName;
        public URL bookURL;

        public BookInfo(String book, String filename) {
            bookName = book;
            bookURL = getClass().getResource(filename);
            if (bookURL == null) {
                System.err.println("Couldn't find file: "
                                   + filename);
            }
        }

        public String toString() {
            return bookName;
        }
    }
	
	private void populateTree(DefaultMutableTreeNode root) {
		DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;

        category = new DefaultMutableTreeNode("Books for Java Programmers");
        root.add(category);

        //original Tutorial
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Tutorial: A Short Course on the Basics",
            "tutorial.html"));
        category.add(book);

        //Tutorial Continued
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Tutorial Continued: The Rest of the JDK",
            "tutorialcont.html"));
        category.add(book);

        //JFC Swing Tutorial
        book = new DefaultMutableTreeNode(new BookInfo
            ("The JFC Swing Tutorial: A Guide to Constructing GUIs",
            "swingtutorial.html"));
        category.add(book);

        //Bloch
        book = new DefaultMutableTreeNode(new BookInfo
            ("Effective Java Programming Language Guide",
	     "bloch.html"));
        category.add(book);

        //Arnold/Gosling
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Programming Language", "arnold.html"));
        category.add(book);

        //Chan
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Developers Almanac",
             "chan.html"));
        category.add(book);

        category = new DefaultMutableTreeNode("Books for Java Implementers");
        root.add(category);

        //VM
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Virtual Machine Specification",
             "vm.html"));
        category.add(book);

        //Language Spec
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Language Specification",
             "jls.html"));
        category.add(book);
	}
	
	public class TimeLine extends Component {
		private final List<Sample> samples;
		private int selectDragStart = 0;
		private int selectDragEnd = 0;
		private boolean isDragging = false;
		private float scale = 2.0f;
		private final SampleLog log;

		public TimeLine(final SampleLog log) {
			this.log = log;
			this.samples = log.samples;
			setPreferredSize(new Dimension(samples.size(), 100));
			addMouseWheelListener( new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					scale += e.getUnitsToScroll() * 0.10f;
					validateValues();
				}
			});
			addMouseListener( new MouseListener() {
				public void mouseReleased(MouseEvent e) {
					if( isDragging ) {
						isDragging = false;
						selectionStart = samples.get(selectDragStart);
						selectionEnd = samples.get(selectDragEnd);
						validateValues();
						ViewList.this.update();
					}
				}
				
				public void mousePressed(MouseEvent e) {
					if( e.getButton() == MouseEvent.BUTTON1 ) {
						int x = (int) (e.getX() / scale);
						if( x > 0 && x < samples.size() ) {
							isDragging = true;
							selectDragStart = x;
							selectDragEnd = x;
							validateValues();
						}
					}
				}
				
				public void mouseExited(MouseEvent e) {
					
				}
				
				public void mouseEntered(MouseEvent e) {
					
				}
				
				public void mouseClicked(MouseEvent e) {
					
				}
			});
			addMouseMotionListener( new MouseMotionListener() {
				
				public void mouseMoved(MouseEvent e) {
					
				}
				
				public void mouseDragged(MouseEvent e) {
					if( isDragging == false ) return;
					int x = (int) (e.getX() / scale);
					if( x > 0 && x < samples.size() ) {
						if( x < selectDragStart ) {
							selectDragStart = x;
						} else {
							selectDragEnd = x;
						}
						validateValues();
					}
				}
			});
		}
		
		private void validateValues() {
			if( selectDragStart > selectDragEnd ) {
				int old = selectDragStart;
				selectDragStart = selectDragEnd;
				selectDragEnd = old;
			}
			if( scale < 1 ) scale = 1f;
			if( scale > 100 ) scale = 100f;
			System.out.println(scale);
			repaint();
		}
		
		public int maxDepth() {
			int max = 0;
			for (Sample s : samples) {
				max = Math.max(max, s.getStack().size());
			}
			return max;
		}

		private boolean hasSelectionRange() {
			return getSelectionIndex(selectionStart) != -1 && getSelectionIndex(selectionEnd) != -1;
		}
		
		private int getSelectionIndex(Object selectObj) {
			if( selectObj == null ) {
				return -1;
			}
			if( samples.indexOf(selectObj) != -1 ) {
				return samples.indexOf(selectObj);
			}
			for (int i = 0; i < samples.size(); i++) {
				List<Marker> markers = samples.get(i).getMarkers();
				for(Marker m: markers) {
					if( selectObj == m ) {
						return i;
					}
				}
			}
			
			return -1;
		}
		
		public boolean isMatchingSample(Sample s) {
			if( s == null || highlightMatchingStack == null ) {
				return false;
			}
			if( s.getStack().size() != highlightMatchingStack.size() ) {
				return false;
			}
			
			for(int i = 0; i < highlightMatchingStack.size(); i++) {
				if( highlightMatchingStack.get(i).equalsIgnoreCase(s.getStack().get(i)) == false ) {
					return false;
				}
			}
			return true;
		}
		public void paint(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());
			int x = 0;
			g.setColor(Color.black);
			for(Sample s: samples) {
				if( isMatchingSample(s) ) {
					g.setColor(Color.red);
				} else {
					g.setColor(Color.black);
				}
				g.fillRect((int)(x++ * scale), 0, (int)(1 * scale), s.getStack().size() * getHeight() / maxDepth());
			}

			if( hasSelectionRange() ) {
				g.setColor(new Color(0f, 0.0f, 1f, 0.4f));
				g.fillRect((int)(getSelectionIndex(selectionStart) * scale), 0, (int)((getSelectionIndex(selectionEnd) - getSelectionIndex(selectionStart)) * scale), getHeight());
			}
			
			g.setColor(new Color(0f, 1.0f, 0f, 0.4f));
			if( isDragging ) {
				g.fillRect((int)(selectDragStart * scale), 0, (int)((selectDragEnd - selectDragStart) * scale), getHeight());
			}
			
		}
	}
}
