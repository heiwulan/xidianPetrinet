/**
 * 
 */
package edu.xidian.petrinet.test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.jagal.graph.exception.VertexNotFoundException;
import de.uni.freiburg.iig.telematik.jagal.traverse.TraversalUtils;
import de.uni.freiburg.iig.telematik.jagal.traverse.Traverser;
import de.uni.freiburg.iig.telematik.jagal.traverse.Traverser.TraversalMode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPetriNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;
import edu.xidian.petrinet.PetriNetTraversalUtils;
import edu.xidian.petrinet.S2PR;

/**
 * @author Administrator
 *
 */
public class PetriNetTraversalUtilsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * 获取强连通分量
	 */
	//@Test
	public void getStronglyConnectedComponents1() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p1");

		System.out.println("ptnet1：" + ptnet1);
		
		Set<Set<AbstractPNNode<?>>> Components;
		Components = PetriNetTraversalUtils.getStronglyConnectedComponents(ptnet1);
		
		System.out.println("Components: " + Components);
		
		System.out.println("size=" + Components.size());
		System.out.println("isEmpty=" + Components.isEmpty());
		assertEquals(1, Components.size()); // 一个强连通分量
		assertTrue(!Components.isEmpty()); // 非空
		
		// 遍历各强连通分量
		Iterator<Set<AbstractPNNode<?>>> iter1 = Components.iterator(); 
		while (iter1.hasNext()) {
		  Set<AbstractPNNode<?>> nodes = iter1.next();
		  System.out.println("contains p1 = " + nodes.contains(ptnet1.getPlace("p1")));
		  Iterator<AbstractPNNode<?>> iter2 = nodes.iterator();
		  while(iter2.hasNext()) {
			  AbstractPNNode<?> node = iter2.next();
			  System.out.print(node + ",");
		  }
		  System.out.println("");
		}  
		
		// 删除p2->t2, 成为p1->t1->p2->t2,有4个本身节点的强连通分量
		PetriNetTraversalUtils.removeRelations(ptnet1, "p1", "t2");;
		System.out.println("\n删除p2->t2后的ptnet1：" + ptnet1);
		
		Components = PetriNetTraversalUtils.getStronglyConnectedComponents(ptnet1);
		System.out.println("Components: " + Components);
		
		System.out.println("size=" + Components.size());
		System.out.println("isEmpty=" + Components.isEmpty());
		assertEquals(4, Components.size()); // 一个强连通分量
		assertTrue(!Components.isEmpty()); // 非空
	}
	
	/**
	 * 获取强连通分量, S2P(simple sequential process)，一个包含全部节点的连通分量，这里是1个含p1的回路
	 */
	//@Test
	public void getStronglyConnectedComponents2() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p1");

		System.out.println("ptnet1：" + ptnet1);
		
		Set<Set<AbstractPNNode<?>>> Components;
		Components = PetriNetTraversalUtils.getStronglyConnectedComponents(ptnet1);
		
		System.out.println("Components: " + Components); // [[t3[t3], t2[t2], p1[p1], t1[t1], p3[p3], p2[p2]]]
		System.out.println("size=" + Components.size()); // 1
		System.out.println("isEmpty=" + Components.isEmpty()); // false
		assertEquals(1, Components.size()); // 一个强连通分量
		assertTrue(!Components.isEmpty()); // 非空
		
		Iterator<Set<AbstractPNNode<?>>> iter1 = Components.iterator(); 
		while (iter1.hasNext()) {
		  Set<AbstractPNNode<?>> nodes = iter1.next();
		  System.out.println("contains p1 = " + nodes.contains(ptnet1.getPlace("p1"))); // true
		  Iterator<AbstractPNNode<?>> iter2 = nodes.iterator();
		  while(iter2.hasNext()) {
			  AbstractPNNode<?> node = iter2.next();
			  System.out.print(node + ","); // t3[t3],t2[t2],p1[p1],t1[t1],p3[p3],p2[p2],
		  }
		  System.out.println("");
		}  
		
		// 结论，一个包含全部节点的连通分量
		boolean ok = false;
		if (Components.size() == 1) {
		   Set<AbstractPNNode<?>> nodes = Components.iterator().next();
		   ok = nodes.size() == ptnet1.nodeCount(); 
		}
		System.out.println("结论，一个包含全部节点的连通分量：" + ok);
	}
	
	/**
	 * 获取强连通分量, S2P(simple sequential process)，一个包含全部节点的连通分量，这里是2个含p1的回路
	 */
	//@Test
	public void getStronglyConnectedComponents3() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p1");
		
		ptnet1.addTransition("tt1");
		ptnet1.addPlace("pp2");
		ptnet1.addTransition("tt2");
		ptnet1.addPlace("pp3");
		ptnet1.addTransition("tt3");
		
		ptnet1.addFlowRelationPT("p1", "tt1");
		ptnet1.addFlowRelationTP("tt1", "pp2");
		ptnet1.addFlowRelationPT("pp2", "tt2");
		ptnet1.addFlowRelationTP("tt2", "pp3");
		ptnet1.addFlowRelationPT("pp3", "tt3");
		ptnet1.addFlowRelationTP("tt3", "p1");

		System.out.println("ptnet1：" + ptnet1);
		
		Set<Set<AbstractPNNode<?>>> Components;
		Components = PetriNetTraversalUtils.getStronglyConnectedComponents(ptnet1);
		
		System.out.println("Components: " + Components); // [[t3[t3], t2[t2], p1[p1], t1[t1], p3[p3], p2[p2]]]
		System.out.println("size=" + Components.size()); // 1
		System.out.println("isEmpty=" + Components.isEmpty()); // false
		assertEquals(1, Components.size()); // 一个强连通分量
		assertTrue(!Components.isEmpty()); // 非空
		
		Iterator<Set<AbstractPNNode<?>>> iter1 = Components.iterator(); 
		while (iter1.hasNext()) {
		  Set<AbstractPNNode<?>> nodes = iter1.next();
		  System.out.println("contains p1 = " + nodes.contains(ptnet1.getPlace("p1"))); // true
		  Iterator<AbstractPNNode<?>> iter2 = nodes.iterator();
		  while(iter2.hasNext()) {
			  AbstractPNNode<?> node = iter2.next();
			  System.out.print(node + ","); // t3[t3],t2[t2],p1[p1],t1[t1],p3[p3],p2[p2],
		  }
		  System.out.println("");
		} 
		
		// 结论，一个包含全部节点的连通分量
		boolean ok = false;
		if (Components.size() == 1) {
		   Set<AbstractPNNode<?>> nodes = Components.iterator().next();
		   ok = nodes.size() == ptnet1.nodeCount(); 
		}
		System.out.println("结论，一个包含全部节点的连通分量：" + ok);		
	}
	
	/**
	 * 含p1的回路:1个
	 */
	//@Test
	public void cycle1() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p1");

		System.out.println("ptnet1：" + ptnet1);
		
		int count = PetriNetTraversalUtils.dfsCheckCycles(ptnet1,ptnet1.getPlace("p1"));
		System.out.println("Cycles：" + count);
		assertEquals(1, count);
	}
	
	
	/**
	 * 含p1的回路:2个
	 */
	@Test
	public void cycle2() {
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p1");
		
		ptnet1.addTransition("tt1");
		ptnet1.addPlace("pp2");
		ptnet1.addTransition("tt2");
		ptnet1.addPlace("pp3");
		ptnet1.addTransition("tt3");
		
		ptnet1.addFlowRelationPT("p1", "tt1");
		ptnet1.addFlowRelationTP("tt1", "pp2");
		ptnet1.addFlowRelationPT("pp2", "tt2");
		ptnet1.addFlowRelationTP("tt2", "pp3");
		ptnet1.addFlowRelationPT("pp3", "tt3");
		ptnet1.addFlowRelationTP("tt3", "p1");
		System.out.println("ptnet1：" + ptnet1);
		
		int count = PetriNetTraversalUtils.dfsCheckCycles(ptnet1,ptnet1.getPlace("p1"));
		System.out.println("Cycles：" + count);
		assertEquals(2, count);
	}

}