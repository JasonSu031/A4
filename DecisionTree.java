/**
 * This class enables the construction of a decision tree
 * 
 * @author Mehrdad Sabetzadeh, University of Ottawa
 * @author Guy-Vincent Jourdan, University of Ottawa
 *
 */

public class DecisionTree {

	private static class Node<E> {
		E data;
		Node<E>[] children;

		Node(E data) {
			this.data = data;
		}
	}

	Node<VirtualDataSet> root;

	/**
	 * @param data is the training set (instance of ActualDataSet) over which a
	 *             decision tree is to be built
	 */
	public DecisionTree(ActualDataSet data) {
		root = new Node<VirtualDataSet>(data.toVirtual());
		build(root);
	}

	/**
	 * The recursive tree building function
	 * 
	 * @param node is the tree node for which a (sub)tree is to be built
	 */
	@SuppressWarnings("unchecked")
	private void build(Node<VirtualDataSet> node) {
		// WRITE YOUR CODE HERE!
		//Case 1

		if(node != null && node.data!=null){
			if(node.data.getNumberOfAttributes()>0){
				if(node.data.getNumberOfDatapoints()>0){

				}else{
					return;
				}
			}else{
				return;
			}
		}else{
			return;
		}

		//Case 2

		if(node.data.getNumberOfAttributes()==1){
			return;
		}
		if(node.data.getUniqueAttributeValues(node.data.getNumberOfAttributes()-1).length==1){
			return;
		}
		boolean cannotSplit = true;
		for(int i =0; i<node.data.getNumberOfAttributes()-1; i++){
			if(node.data.getUniqueAttributeValues(i).length>1){
				cannotSplit = false;
			}
		}
		if(cannotSplit) return;

		//Case 3
		GainInfoItem gains = InformationGainCalculator.calculateAndSortInformationGains(node.data)[0];
		Attribute a_max = node.data.getAttribute(gains.getAttributeName());
		VirtualDataSet[] split; 

		if(a_max.getType()==AttributeType.NOMINAL){
			split = node.data.partitionByNominallAttribute(node.data.getAttributeIndex(a_max.getName()));
		}else{
			int valueIndex = -1;
			String values []= node.data.getUniqueAttributeValues(node.data.getAttributeIndex(a_max.getName()));
			for(int i =0; i<values.length; i++){
				if(values[i].equals(gains.getSplitAt()))valueIndex = i;
			}
			split = node.data.partitionByNumericAttribute(node.data.getAttributeIndex(a_max.getName()), valueIndex);
		}	
		node.children = new Node [split.length];

		for(int i= 0; i<split.length; i++){
			node.children[i] = new Node<VirtualDataSet>(split[i]);
		}
		for(int i =0; i<node.children.length; i++){
			build(node.children[i]);
		}

	}

	@Override
	public String toString() {

		return toString(root, 0);
	}

	/**
	 * The recursive toString function
	 * 
	 * @param node        is the tree node for which an if-else representation is to
	 *                    be derived
	 * @param indentDepth is the number of indenting spaces to be added to the
	 *                    representation
	 * @return an if-else representation of node
	 */
	private String toString(Node<VirtualDataSet> node, int indentDepth) {
		StringBuffer result = new StringBuffer();
		result.append(System.lineSeparator());
		
		if(node.data.getNumberOfAttributes()==1){
			System.out.println("done");                                                               //add base cases and correct this case
			return null;
		}
		if(node.data.getUniqueAttributeValues(node.data.getNumberOfAttributes()-1).length==1){
			result.append(createIndent(indentDepth));
			result.append(node.data.getAttribute(node.data.getNumberOfAttributes()-1).getName()+" = ");
			String [] s = node.data.getUniqueAttributeValues(node.data.getNumberOfAttributes()-1);
			result.append(s[0]);
			result.append(System.lineSeparator());
			return result.toString();
		}

		for(int i =0; i<node.children.length; i++){
			if(i==0){
				result.append(createIndent(indentDepth));
				result.append("if ("+node.children[i].data.getCondition()+") {");
				result.append(toString(node.children[i], ++indentDepth));
				result.append(createIndent(--indentDepth));
				result.append("}");
				result.append(System.lineSeparator());
			}
			else {
				result.append(createIndent(indentDepth));
				result.append("else if ("+node.children[i].data.getCondition()+") {");
				result.append(toString(node.children[i], ++indentDepth));
				result.append(createIndent(--indentDepth));
				result.append("}");
				result.append(System.lineSeparator());

			}
		}
		return result.toString();
	}

	/**
	 * @param indentDepth is the depth of the indentation
	 * @return a string containing indentDepth spaces; the returned string (composed
	 *         of only spaces) will be used as a prefix by the recursive toString
	 *         method
	 */
	private static String createIndent(int indentDepth) {
		if (indentDepth <= 0) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indentDepth; i++) {
			buffer.append(' ');
		}
		return buffer.toString();
	}

	public static void main(String[] args) throws Exception {
	
		StudentInfo.display();

		if (args == null || args.length == 0) {
			System.out.println("Expected a file name as argument!");
			System.out.println("Usage: java DecisionTree <file name>");
			return;
		}

		String strFilename = args[0];

		ActualDataSet data = new ActualDataSet(new CSVReader(strFilename));

		DecisionTree dtree = new DecisionTree(data);

		System.out.println(dtree);
	}
}