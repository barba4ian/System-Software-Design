class TrieNode{
    TrieNode[] children = new TrieNode[26];
    boolean end;
}
class StreamChecker {
     TrieNode parent;
     List<Character> chars;

    public StreamChecker(String[] words) {
        parent = new TrieNode();
        chars = new ArrayList<>();
        for(int i=0; i<words.length; i++){
            TrieNode root = parent;
            for(int j=words[i].length()-1; j>=0; j--){
                int index = words[i].charAt(j) - 'a';
                if(root.children[index] == null){
                    root.children[index] = new TrieNode();
                }
                root = root.children[index];
                if(j == 0){
                        root.end = true;
                }
            }
        }
        
    }
    
    public boolean query(char letter) {
        chars.add(letter);
        TrieNode curr = parent;
        for(int i=chars.size()-1; i>=0; i--){
            int idx = chars.get(i) - 'a';
            if(curr.children[idx]!= null){
                if(curr.children[idx].end == true){
                    return true;
                }
                curr = curr.children[idx];
            }else{
                return false;
            }
        }
        return false;
    }
}

/**
 * Your StreamChecker object will be instantiated and called as such:
 * StreamChecker obj = new StreamChecker(words);
 * boolean param_1 = obj.query(letter);
 */