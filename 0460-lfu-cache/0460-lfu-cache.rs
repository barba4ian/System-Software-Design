use std::cell::RefCell;
use std::collections::HashMap;
use std::rc::{Rc, Weak};

type NodeRef = Rc<RefCell<Node>>;

struct Node {
    key: i32,
    value: i32,
    freq: i32,
    prev: Option<Weak<RefCell<Node>>>,
    next: Option<NodeRef>,
}

impl Node {
    fn new(key: i32, value: i32) -> Self {
        Node {
            key,
            value,
            freq: 0,
            prev: None,
            next: None,
        }
    }
}

struct DList {
    head: NodeRef,
    tail: NodeRef,
    size: i32,
}

impl DList {
    fn new() -> Self {
        let head = Rc::new(RefCell::new(Node::new(-1, -1)));
        let tail = Rc::new(RefCell::new(Node::new(-1, -1)));
        head.borrow_mut().next = Some(Rc::clone(&tail));
        tail.borrow_mut().prev = Some(Rc::downgrade(&head));
        DList {
            head,
            tail,
            size: 0,
        }
    }

    fn push_front(&mut self, node: NodeRef) {
        let first = self.head.borrow().next.clone().unwrap();
        node.borrow_mut().prev = Some(Rc::downgrade(&self.head));
        node.borrow_mut().next = Some(Rc::clone(&first));
        self.head.borrow_mut().next = Some(Rc::clone(&node));
        first.borrow_mut().prev = Some(Rc::downgrade(&node));
        self.size += 1;
    }

    fn remove(&mut self, node: &NodeRef) {
        let prev = node
            .borrow()
            .prev
            .clone()
            .unwrap()
            .upgrade()
            .unwrap();
        let next = node.borrow().next.clone().unwrap();
        prev.borrow_mut().next = Some(Rc::clone(&next));
        next.borrow_mut().prev = Some(Rc::downgrade(&prev));
        node.borrow_mut().prev = None;
        node.borrow_mut().next = None;
        self.size -= 1;
    }

    fn pop_back(&mut self) -> Option<NodeRef> {
        if self.size == 0 {
            return None;
        }
        let last = self.tail.borrow().prev.clone().unwrap().upgrade().unwrap();
        self.remove(&last);
        Some(last)
    }

    fn is_empty(&self) -> bool {
        self.size == 0
    }
}

struct LFUCache {
    capacity: i32,
    min_freq: i32,
    key_node: HashMap<i32, NodeRef>,
    freq_list: HashMap<i32, DList>,
}

impl LFUCache {
    fn new(capacity: i32) -> Self {
        LFUCache {
            capacity,
            min_freq: 0,
            key_node: HashMap::new(),
            freq_list: HashMap::new(),
        }
    }

    fn touch(&mut self, node: &NodeRef) {
        let freq = node.borrow().freq;
        {
            let list = self.freq_list.get_mut(&freq).unwrap();
            list.remove(node);
            if list.is_empty() && self.min_freq == freq {
                self.min_freq += 1;
            }
        }
        let new_freq = freq + 1;
        node.borrow_mut().freq = new_freq;
        let list = self.freq_list.entry(new_freq).or_insert_with(DList::new);
        list.push_front(Rc::clone(node));
    }

    fn get(&mut self, key: i32) -> i32 {
        if let Some(node) = self.key_node.get(&key).cloned() {
            let value = node.borrow().value;
            self.touch(&node);
            value
        } else {
            -1
        }
    }

    fn put(&mut self, key: i32, value: i32) {
        if self.capacity == 0 {
            return;
        }

        if let Some(node) = self.key_node.get(&key).cloned() {
            node.borrow_mut().value = value;
            self.touch(&node);
            return;
        }

        if self.key_node.len() as i32 == self.capacity {
            if let Some(list) = self.freq_list.get_mut(&self.min_freq) {
                if let Some(evict) = list.pop_back() {
                    let evict_key = evict.borrow().key;
                    self.key_node.remove(&evict_key);
                }
            }
        }

        let node = Rc::new(RefCell::new(Node::new(key, value)));
        node.borrow_mut().freq = 1;
        self.key_node.insert(key, Rc::clone(&node));
        let list = self.freq_list.entry(1).or_insert_with(DList::new);
        list.push_front(node);
        self.min_freq = 1;
    }
}

/**
 * Your LFUCache object will be instantiated and called as such:
 * let obj = LFUCache::new(capacity);
 * let ret_1: i32 = obj.get(key);
 * obj.put(key, value);
 */