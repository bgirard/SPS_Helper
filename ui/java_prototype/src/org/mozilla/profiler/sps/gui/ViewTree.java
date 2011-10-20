package org.mozilla.profiler.sps.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.JScrollPane;

import org.mozilla.profiler.sps.SampleLog;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class ViewTree extends View {


	public ViewTree(SampleLog log) {
		super(log);
		final mxGraph graph = new mxGraph();
		graph.setVertexLabelsMovable(false);
		graph.setCellsMovable(false);
		graph.setEdgeLabelsMovable(false);
		graph.setCellsBendable(false);
		graph.setEnabled(false);
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try
		{
			Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
					30);
			Object v2 = graph.insertVertex(parent, null, "World!",
					240, 150, 80, 30);
			graph.insertEdge(parent, null, "Edge", v1, v2);
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		final mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graphComponent.setEnabled(false);
		JScrollPane scrollPane = new JScrollPane(graphComponent);
		getContentPane().add(scrollPane);

		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{

			public void mouseReleased(MouseEvent e)
			{
				Object cell = graphComponent.getCellAt(e.getX(), e.getY());

				if (cell != null)
				{
					System.out.println("cell="+graph.getLabel(cell));
				}
			}
		});

		
	}

}
