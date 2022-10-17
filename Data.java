/**
 * @author Aneesh Patnaik
 * Data objects hold a character and its frequency in the text
 */

public class Data implements Comparable {
    private Character character;
    private Integer frequency;

    public Data(Character character, Integer freq){
        this.character = character;
        this.frequency = freq;
    }

    public Character getCharacter() {
        return this. character;
    }

    public Integer getFrequency(){
        return this.frequency;
    }

    @Override
    public String toString() {
        return character + ":" + frequency;
    }
    /**
     * Overrides the comparison function, to be used when constructing the tree priority queue
     * @param other the Data to be compared
     * @return the comparison of the frequencies
     */
    @Override
    public int compareTo(Object other) {
        if(this.frequency < ((Data)other).frequency){
            return -1;
        } else if (this.frequency.equals(((Data) other).frequency)){
            return 0;
        } else {
            return 1;
        }
    }
}