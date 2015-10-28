package BPlusTree;

public class TreeNode  {
	    private static final int M = 4;    // max children per B-tree node = M-1

	    private Node root;             // root of the B-tree
	    private int H;                // height of the B-tree
	    private int N;                 // number of key-value pairs in the B-tree

	    // helper B-tree node data type
	    private static final class Node {
	        private int m;                             // number of children
	        private Entry[] children = new Entry[M];   // the array of children

	        // create a node with k children
	        private Node(int k) {
	            m = k;
	        }
	    }

	    // internal nodes: only use key and next
	    // external nodes: only use key and value
	    private static class Entry {
			private String data_value;
	        private String key;
	        private Node next;     // helper field to iterate over array entries
	        public Entry(String data_value, String key, Node next) {
	            this.data_value   = data_value;
	            this.key = key;
	            this.next  = next;
	        }
	    }

	    // constructor
	    public TreeNode() {
	        root = new Node(0);
	    }
	 
	    // return number of key-value pairs in the B-tree
	    public int size() { return N; }

	    // return height of B-tree
	    public int height() { return H; }


	    // search for given key, return associated value; return null if no such key
	    public String get(String data_value) { return search(root, data_value, H); }
		
	    private String search(Node x, String data_value, int h) {
	        Entry[] children = x.children;

	        // external node
	        if (h == 0) {
	            for (int j = 0; j < x.m; j++) {
	                if (data_value.equals(children[j].data_value)) return (String) children[j].key;
	            }
	        }

	        // internal node
	        else {
	            for (int j = 0; j < x.m; j++) {
	                if (j+1 == x.m || data_value.toString().compareTo((children[j+1].data_value).toString())<0){
	                    return search(children[j].next, data_value, h-1);
	            }
	        }
	        }
	        return null;
	    }


	    // insert key-value pair
	    // add code to check for duplicate keys
	    public void put(String data_value, String key) {
			if(get(data_value)!= null) {append(root, data_value, key, H);}
			
			else{
				Node u = insert(root, data_value, key, H); 
				N++;
				if (u == null) return;

				// need to split root
				Node t = new Node(2);
				t.children[0] = new Entry(root.children[0].data_value, null, root);
				t.children[1] = new Entry(u.children[0].data_value, null, u);
				root = t;
				H++;
			}
	    }
		
		private void append(Node x, String data_value, String key,int h){
			Entry[] children = x.children;
			
			if (h == 0) {
	            for (int j = 0; j < x.m; j++) {
	                if (data_value.equals(children[j].data_value)){
						children[j].key = children[j].key.concat(key);
					}
	            }
	        }

	        // internal node
	        else {
	            for (int j = 0; j < x.m; j++) {
	                if (j+1 == x.m || data_value.compareTo((String) children[j+1].data_value)<0){
						append(children[j].next, data_value,key, h-1);
					}   
	            }
	        }
		}
		
		
	    private Node insert(Node x, String data_value, String key, int h) {
	        int j;
	        Entry t = new Entry(data_value, key, null);

	        // external node
	        if (h == 0) {
	            for (j = 0; j < x.m; j++) {
	                if (data_value.compareTo((String) x.children[j+1].data_value)<0) break;
	            }
	        }

	        // internal node
	        else {
	            for (j = 0; j < x.m; j++) {
	                if ((j+1 == x.m) || data_value.compareTo((String) x.children[j+1].data_value)<0) {
	                    Node u = insert(x.children[j++].next, data_value, key, h-1);
	                    if (u == null) return null;
	                    t.data_value = u.children[0].data_value;
	                    t.next = u;
	                    break;
	                }
	            }
	        }

	        for (int i = x.m; i > j; i--)
	            x.children[i] = x.children[i-1];
	        x.children[j] = t;
	        x.m++;
	        if (x.m < M) return null;
	        else         return split(x);
	    }

	    // split node in half
	    private Node split(Node x) {
	        Node t = new Node(M/2);
	        x.m = M/2;
	        for (int j = 0; j < M/2; j++)
	            t.children[j] = x.children[M/2+j]; 
	        return t;    
	    }
		
		public void remove(String data_value){
			Object[] k = new Object[2];
			int j;
//			if(get(data_value)== null) { 
//				return;
//			}
//			else{
			k = delete(null,root,null,data_value,H);
			if(k==null) return;
			else{
				for (j = 0; j < root.m; j++) {
					if (data_value.equals(root.children[j].data_value)) break;
						
				}
				if(k[1]=="replace"){
					root.children[j].data_value = (String) k[0];
		            return;
				}else{
					for (int i = j; i <= root.m; i++){
						root.children[i] = root.children[i+1];
					}
					root.m--;
					if (root.m >= M/2) return;
					else{
						Node t = root.children[0].next;
						Node u = root.children[1].next;
						for(int i=t.m;i<M;i++){
							t.children[i] = u.children[i-t.m];
						}
						t.m = t.m+u.m;
						u = null;
						Node newroot = new Node(t.m);
						newroot = t;
						root = newroot;
						H--;
					}
				}
				
			}
			
		}
		
		private Object[] delete(Node l,Node x,Node r,String data_value,int h){
			Object[] values = new Object[2];
			Object[] k = new Object[2];
			
			Entry[] children = x.children;
			String keynew = data_value; 
			int j;
			int p;
			String s = null;
			
			if (h == 0) {
	            for (j = 0; j < x.m; j++) {
	                if (data_value.equals(children[j].data_value)) break;
	            }
	        }

	        // internal node
	        else {
	            for (j = 0; j < x.m; j++) {
	                if (j+1 == x.m || data_value.compareTo((String) children[j+1].data_value)<0)
						if (j==0){
							k = delete(null,x.children[j].next,x.children[j+1].next, data_value, h-1);
						}else if(j+1==x.m){
							k = delete(x.children[j-1].next,x.children[j].next,null, data_value, h-1);
						}else{
							k = delete(x.children[j-1].next,x.children[j].next,children[j+1].next, data_value, h-1);
						}
						if (k[0] == null) return null;
	                    keynew = (String) k[1];
						s = (String) k[2];
	                    break; 
	            }
	        }
			
			p=j;
			if (p>x.m/2){p=1;}
			else{p=0;}
			
			if(s==null){
				for (int i = j; i <= x.m; i++){
					children[i] = children[i+1];
				}
				x.m--;
				if (x.m >= M/2) return null;
				else{ 
					values[0] =  merge(l,x,r,p)[0];
					values[1] =  merge(l,x,r,p)[1];
					return values;
				}
			}else{
				children[j].data_value = keynew;
				return null;
				
			}
		}
			
		
		private Object[] merge(Node l,Node x,Node r,int p){
			Object[] values = new Object[2];
			Entry[] childrenL = l.children;
			Entry[] childrenR = r.children;
			Entry[] childrenX = x.children;
			String s;
			
			if(p==1||l==null){
				if(r.m>= M/2){
					childrenX[x.m] = childrenR[0];
					childrenX[x.m].next = childrenR[0].next;
					for(int i=0;i<r.m-1;i--){
						childrenR[i]=childrenR[i+1];
						childrenR[i].next=childrenR[i+1].next;
					}
					r.m--;
					s = "replace";
					values[0] = childrenR[0].data_value;
				}else{
					for(int i=x.m;i<(x.m+r.m);i++){
						childrenX[i]=childrenR[i-x.m];
//						childrenX[i].next=childrenR[i-x.m].next;
					}
					x.m = x.m+r.m;
					r = null;
					s = null;
					values[0] = childrenX[x.m].data_value;
				}
			}else{
				if(l.m>= M/2){
					for(int i=1;i<=x.m;i++){
						childrenX[i]=childrenX[i-1];
//						childrenX[i].next=childrenX[i-1].next;
					}
					childrenX[0] = childrenL[l.m-1];
//					childrenX[0].next = childrenL[l.m-1].next;
					l.children[l.m-1] = null;
					l.m--;
					s = "replace";
					values[0] = childrenX[0].data_value;
				}else{
					for(int i=l.m;i<(x.m+l.m);i++){
						childrenL[i]=childrenX[i-l.m];
//						childrenL[i].next=childrenX[i-l.m].next;
					}
					l.m = x.m+l.m;
					x = null;
					s = null;
					values[0] = childrenL[l.m].data_value;
				}
			}
			values[1] = s;
			return values;
		}
	}
		
		


