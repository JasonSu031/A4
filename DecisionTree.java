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

		if(node != null && node.data!=null){ // checking whether the node and node.data is empty or not 
			if(node.data.getNumberOfAttributes()>0){ // checking if it has at least 1 attributes inside of node.data
				if(node.data.getNumberOfDatapoints()>0){ // checking if it has at least 1 datapoint in the data 

				}else{
					return;
				}
			}else{
				return;
			}
		}else{
			throw new NullPointerException("It's empty");
			}

		//Case 2

		if(node.data.getNumberOfAttributes()==1){// 2.1 node.data has only 1 attribute
			return; // we do can't split the data; stop right here 
		}
		if(node.data.getUniqueAttributeValues(node.data.getNumberOfAttributes()-1).length==1){ // 2.2 if number of attribute larger than 1; we check the last element of attribute "class" 
			return; // if it only contain only 1 datapoints; then it not neccessary to split again 
		}
		boolean cannotSplit = true;
		for(int i =0; i<node.data.getNumberOfAttributes()-1; i++){ // 2.3 checking other non-class attribute, whether the it contains more than 1 datapoints
			if(node.data.getUniqueAttributeValues(i).length>1){
				cannotSplit = false; // checking whether it contains 1 datapoint or not 
			}
		}
		if(cannotSplit) return; // if all the attributes that contains single datapoint, then it do not go to split 

		//Case 3  all the attributes has more than 1 datapoints and class attributes doesn't contains single datapoints; we do split in here 
		GainInfoItem gains = InformationGainCalculator.calculateAndSortInformationGains(node.data)[0]; // which attribute to split determines based on the entropy gains
		Attribute a_max = node.data.getAttribute(gains.getAttributeName()); // based on the gains value to find the attribute 
		VirtualDataSet[] split; // create a new virtualDataSet array to store partitions 

		if(a_max.getType()==AttributeType.NOMINAL){ // nominal case of partition 
			split = node.data.partitionByNominallAttribute(node.data.getAttributeIndex(a_max.getName())); // using getter method to get the attribute name and it index; then using the method of partitionByNominallAttribute in VirtualDataSet
		}else{ // numeric case of partition 
			int valueIndex = -1; // instantiate the valueIndex integer 
			String values []= node.data.getUniqueAttributeValues(node.data.getAttributeIndex(a_max.getName())); // create a string array to contains the a_max's unique datapoints 
			for(int i =0; i<values.length; i++){ // looping over the array of values 
				if(values[i].equals(gains.getSplitAt()))valueIndex = i; // finding the a_max index by compare the index of each dataPoints in the numeric attribute and a_max value 
			}
			split = node.data.partitionByNumericAttribute(node.data.getAttributeIndex(a_max.getName()), valueIndex);
			// using getter method to get the attribute name and it split at index; then using the method of partitionByNumericAttribute in VirtualDataSet
		}	


		node.children = new Node [split.length]; // instantiate the array of children that in the static class Node with the size that number of partition generated at that time 

		for(int i= 0; i<split.length; i++){ // looping over  to append each partition in to children array 
			node.children[i] = new Node<VirtualDataSet>(split[i]); // create a new node data and sign it to children array elements 
		}
		for(int i =0; i<node.children.length; i++){ // looping over the children array, and calling build to do each partition 
			build(node.children[i]); // recursion to build for each partition 
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
		
		if(node.data.getNumberOfAttributes()==1){ // only 1 attribute in data, then no need to do if/else if                                                            //add base cases and correct this case
			throw new NullPointerException();
		}
		if(node.data.getUniqueAttributeValues(node.data.getNumberOfAttributes()-1).length==1){ // checking the last attribute, whether it has unique datapoints or not 
			result.append(createIndent(indentDepth)); 
			result.append(node.data.getAttribute(node.data.getNumberOfAttributes()-1).getName()+" = ");// getting the last element name 
			String [] s = node.data.getUniqueAttributeValues(node.data.getNumberOfAttributes()-1); // getting the last attribute unique datapoint
			result.append(s[0]); // add the datapoints to the string 
			result.append(System.lineSeparator()); // append line 
			return result.toString(); // return this string to the main string 
		}

		for(int i =0; i<node.children.length; i++){  // looping over the children array
			if(i==0){ // the first condition of 'if'
				result.append(createIndent(indentDepth)); 
				result.append("if ("+node.children[i].data.getCondition()+") {"); 
				result.append(toString(node.children[i], ++indentDepth));// indent increase base on number of recursion time
				result.append(createIndent(--indentDepth)); // indent need to be 0 at the end to close first statement 
				result.append("}");
				result.append(System.lineSeparator());
			}
			else { // after if and consecutive term condition using 'else if '
				result.append(createIndent(indentDepth));
				result.append("else if ("+node.children[i].data.getCondition()+") {");
				result.append(toString(node.children[i], ++indentDepth)); // indent increase base on number of recursion time
				result.append(createIndent(--indentDepth));// indent need to be 0 at the end to close first statement 
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