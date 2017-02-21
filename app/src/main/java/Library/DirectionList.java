package Library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Models.Direction;
import Models.DirectionCreation;

/**
 * Need this class to make sure Direction.Order is corresponding to DirectionList[i].Order.
 */

public class DirectionList {
    private int maxSize = 20;
    private int currentSize = 0;
    private DirectionCreation[] directions;

    public DirectionList() {
        this.directions = new DirectionCreation[this.maxSize];
    }

    public DirectionListInfo add() {

        // If array is full, return false
        if (this.currentSize >= maxSize)
            return DirectionListInfo.IS_FULL;

        // Init new Direction
        DirectionCreation d = new DirectionCreation(this.currentSize + 1, "");

        // Add direction to array and increase size
        this.directions[this.currentSize] = d;
        this.currentSize += 1;

        return DirectionListInfo.INSERTED;
    }

    public DirectionListInfo add(DirectionCreation directionCreation) {
        if (this.currentSize >= maxSize)
            return DirectionListInfo.IS_FULL;

        this.directions[this.currentSize] = directionCreation;
        this.currentSize += 1;

        return DirectionListInfo.INSERTED;
    }

    public DirectionCreation get(int position) {
        if (this.directions[position] != null)
            return this.directions[position];

        return null;
    }

    public DirectionCreation[] getAll() {
        return this.directions;
    }

    public List<DirectionCreation> toList() {
        List<DirectionCreation> list = new ArrayList<DirectionCreation>();

        for (int i = 0; i < this.currentSize; i++)
            list.add(this.get(i));

        return list;
    }

    public DirectionListInfo remove(int order) {
        DirectionListInfo dli = null;

        // Get position corresponding to order
        int position = order - 1;

        // If size is 0, don't do anything
        if (this.currentSize == 0)
            dli = DirectionListInfo.IS_EMPTY;

        // Look for Direction, remove if found
        if (this.directions[position] != null) {
            this.perculateDeletion(position);
            dli = DirectionListInfo.REMOVED;
        } else {
            dli = DirectionListInfo.NOT_FOUND;
        }

        return dli;
    }

    private void perculateDeletion(int start) {

        // Decrease size
        this.currentSize--;

        // Move every Direction from start, back one step
        for (int i = start; i < this.currentSize; i++) {
            this.directions[i] = this.directions[i+1];
            this.directions[i].order = i+1;
        }

        // Set the last step to null so we don't have duplicate data in array
        this.directions[this.currentSize] = null;
    }

    public int size() {
        return this.currentSize;
    }

    public boolean isEmpty() {
        return this.currentSize == 0;
    }

    public static DirectionList convertFromDCList(List<DirectionCreation> directionList) {
        DirectionList dl = new DirectionList();

        for (int i = 0; i < directionList.size(); i++)
            dl.add(directionList.get(i));

        return dl;
    }

    public static DirectionList convertFromDList(List<Direction> directionList) {
        DirectionList dl = new DirectionList();

        for (int i = 0; i < directionList.size(); i++) {
            Direction d = directionList.get(i);
            DirectionCreation dc = new DirectionCreation(d.order, d.description);
            dl.add(dc);
        }

        return dl;
    }

    public static List<Direction> convertToOriginalDirectionList(DirectionList list) {
        List<Direction> directions = new ArrayList<Direction>();

        for (int i = 0; i < list.size(); i++) {
            Direction d = new Direction(list.get(i).order, list.get(i).description);
            directions.add(d);
        }

        return directions;
    }
}
