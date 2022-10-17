import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * @author Aneesh Patnaik
 * CodeTreeGenerators generate the tree of characters and their frequencies,
 * the priority queue that stores these data,
 * the frequency table
 */
public class CodeTreeGenerator {
    BufferedReader input;
    HashMap<Character,Integer> frequencyTable;
    PriorityQueue<BinaryTree<Data>> priorityQueue;
    BinaryTree<Data> codeTree;

    public CodeTreeGenerator(String pathName) throws Exception{
        input = new BufferedReader(new FileReader(pathName));

        genFrequencyTable();
        genPriorityQueue();
        genCodeTree();
    }

    /**
     * Reads a file and generates a table of each character's frequencies
     * @throws Exception if the file doesn't exist
     */
    public void genFrequencyTable() throws Exception{
        frequencyTable = new HashMap<Character, Integer>();

        int intChar = input.read();
        while(intChar != -1){
            char character = (char)intChar;
            intChar = input.read();

            if(frequencyTable.containsKey(character)){
                frequencyTable.put(character, frequencyTable.get(character) + 1);
            }
            else {
                frequencyTable.put(character, 1);
            }
        }

        input.close();
    }

    /**
     * Generates a priority queue of all the characters based on their frequencies
     * and places it into instance variables
     */
    public void genPriorityQueue(){
        priorityQueue = new PriorityQueue<>(
                (BinaryTree<Data> tree1, BinaryTree<Data> tree2) ->
                        tree1.getData().compareTo(tree2.getData())
        );
        for(Character key: frequencyTable.keySet()){
            priorityQueue.add(new BinaryTree<Data>(new Data(key, frequencyTable.get(key))));
        }
    }

    /**
     * Generates a tree using the characters and their frequencies
     * which is used to find and use code words
     */
    public void genCodeTree(){

        if(priorityQueue.size() == 1){
            BinaryTree<Data> tree = priorityQueue.poll();
            Data newData = new Data(null, tree.data.getFrequency());
            BinaryTree<Data> newTree = new BinaryTree<Data>(newData, tree, null);
            priorityQueue.add(newTree);
        }

        while(priorityQueue.size() > 1){
            BinaryTree<Data> tree1 = priorityQueue.poll();
            BinaryTree<Data> tree2 = priorityQueue.poll();

            Data newData = new Data(null, tree1.data.getFrequency() + tree2.data.getFrequency());
            BinaryTree<Data> newTree = new BinaryTree<Data>(newData, tree1, tree2);

            priorityQueue.add(newTree);
        }

        codeTree = priorityQueue.remove();
    }

    public BinaryTree<Data> getCodeTree(){
        return this.codeTree;
    }

}
