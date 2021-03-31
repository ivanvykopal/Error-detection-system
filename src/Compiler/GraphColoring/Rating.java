package Compiler.GraphColoring;
//TODO:dorobiť dokumentáciu
public class Rating implements Comparable {

    private int rate;
    private int position;

    public Rating(int rate, int position) {
        this.rate = rate;
        this.position = position;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return rate;
    }

    public void setPosition(int pos) {
        position = pos;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int compareTo(Object o) {
        int oRate = ((Rating) o).getRate();
        return oRate - this.rate;
    }
}
