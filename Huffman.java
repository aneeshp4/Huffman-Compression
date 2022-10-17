import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

/**
 * @author Aneesh Patnaik
 */
public class Huffman {

    private CodeTreeGenerator codeTree;

    /**
     * Encodes a file and writes the encoded document into filenameEncoded.txt
     * @param filename the file to encode
     * @throws Exception if the filename.txt doesn't exist
     */
    public void encode(String filename) throws Exception {
        // Read the file, encode it, and put it into a new file
        BufferedReader fileReader = new BufferedReader(new FileReader(filename));
        String filenameStripped = filename.substring(0, filename.length() - 4);
        BufferedBitWriter bitWriter = new BufferedBitWriter(filenameStripped + "Encoded.txt");

        // Deals with empty file boundary case
        if(txtToString(filename).equals("")){
            fileReader.close();
            bitWriter.close();
            return;
        }

        // Generates the code tree for the code words
        codeTree = new CodeTreeGenerator(filename);

        // Make the code map from the character to its code word
        HashMap<Character, String> codeMap = new HashMap<>();
        generateCodeMap(codeMap, codeTree.getCodeTree(), "");

        // Reads through the file to encode and writes its encoding into the encoded file
        int intChar = fileReader.read();
        while(intChar != -1) {
            char character = (char) intChar;
            intChar = fileReader.read();
            String codeWord = codeMap.get(character);
            for (char bit : codeWord.toCharArray()) {
                bitWriter.writeBit(bit == '1');
            }
        }

        fileReader.close();
        bitWriter.close();
    }

    /**
     * Takes in a reference to a hashmap from characters to code words and adds all mappings to it
     * @param codeMap a reference to the map from characters to code words
     * @param codePointer a reference to the pointer in the tree; starts at the root and recurses through the tree
     * @param curPath a String used for recursion; should start as an empty string
     */
    private void generateCodeMap(HashMap<Character, String> codeMap, BinaryTree<Data> codePointer, String curPath) {
        if (codePointer.getData().getCharacter() == null) { // If we're on a number node...
            // Recursively searches through the children of the node
            if(codePointer.hasLeft())
                generateCodeMap(codeMap, codePointer.getLeft(), curPath + "0");

            if(codePointer.hasRight())
                generateCodeMap(codeMap, codePointer.getRight(), curPath + "1");
        } else { // We are on a character node
            // There is a character here, add it and the current path to the map
            codeMap.put(codePointer.getData().getCharacter(), curPath);
        }
    }

    /**
     * Decodes an encoded file into plaintext and writes it to filenameDecoded.txt
     * @param filename the name of the file to decode
     */
    public void decode(String filename) throws Exception {
        BufferedBitReader bitReader = new BufferedBitReader(filename);
        String fileNameStripped = filename.substring(0, filename.length() - 11);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileNameStripped + "Decoded.txt"));

        // If we're trying to decode an empty file
        if (codeTree == null) {
            bitReader.close();
            fileWriter.close();
            return;
        }
        // Gets the root of the code tree to begin traversing through it
        BinaryTree<Data> codePointer = codeTree.getCodeTree();
        while (bitReader.hasNext()) {
            boolean bit = bitReader.readBit();
            // Based on the value of the bit, moves the code pointer to its left or right child
            if (bit)
                codePointer = codePointer.getRight();
            else
                codePointer = codePointer.getLeft();

            // If we're at a character node (leaf) then we reset the code pointer and add to the decoded file
            if (codePointer.getData().getCharacter() != null) {
                // We are on a character node in the tree, write it
                fileWriter.write(codePointer.getData().getCharacter());
                codePointer = codeTree.getCodeTree();
            }
        }
        bitReader.close();
        fileWriter.close();
    }

    /**
     * Helper function mainly for testing
     * @param filename the file to turn into a string
     * @return the file's contents as a string
     */
    public String txtToString(String filename) throws Exception{
        BufferedReader fileReader = new BufferedReader(new FileReader(filename));
        String string = "";
        String readNewLine = fileReader.readLine();
        while(readNewLine != null){
            string += readNewLine;
            readNewLine = fileReader.readLine();
        }
        return string;
    }

    /**
     * Tests that a file's encoding and decoding works
     * @param filename the file to test encoding and decoding
     */
    public static void test(String filename) {
        Huffman huffmanCoder = new Huffman();
        try {
            huffmanCoder.encode(filename);
        } catch (Exception e) {
            System.err.println("Could not encode" + filename);
            return;
        }
        String filenameEncoded = filename.substring(0, filename.length() - 4) + "Encoded.txt";
        try {
            huffmanCoder.decode(filenameEncoded);
        } catch (Exception e) {
            System.err.println("Could not decode " + filename);
            return;
        }
        try {
            if (huffmanCoder.txtToString("inputs/hello.txt").equals(huffmanCoder.txtToString("inputs/helloDecoded.txt")))
                System.out.println(filename + " encoding and decoding successful!");
            else
                System.err.println(filename + " encoding and decoding do not match");
        } catch (Exception e) {
            System.err.printf("Could not open %s", filename);
        }
    }

    public static void main(String[] args) throws Exception {
        test("inputs/hello.txt");
        test("inputs/WarAndPeace.txt");
        test("inputs/USConstitution.txt");
        test("inputs/empty.txt");
        test("inputs/SingleCharacter.txt");
        test("inputs/repeated.txt");

    }
}

