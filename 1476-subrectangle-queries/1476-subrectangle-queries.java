class SubrectangleQueries {

    private int[][] rectangle;

    public SubrectangleQueries(int[][] rectangle) {
        // Deep copy to avoid external modification
        int rows = rectangle.length;
        int cols = rectangle[0].length;
        this.rectangle = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(rectangle[i], 0, this.rectangle[i], 0, cols);
        }
    }
    
    public void updateSubrectangle(int row1, int col1, int row2, int col2, int newValue) {
        for (int i = row1; i <= row2; i++) {
            for (int j = col1; j <= col2; j++) {
                rectangle[i][j] = newValue;
            }
        }
    }
    
    public int getValue(int row, int col) {
        return rectangle[row][col];
    }
}