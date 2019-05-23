package com.jacky.practice;

import java.util.HashSet;
import java.util.Set;

//Definition for singly-linked list.
class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }
}

public class T_ListNode {

    //合并多个单有序链表（假设都是递增的）
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {

        if (l1 == null)
            return l2;
        if (l2 == null)
            return l1;

        ListNode mergeNode;

        if (l1.val > l2.val) {
            mergeNode = l2;
            mergeNode.next = mergeTwoLists(l2.next, l1);
        } else {
            mergeNode = l1;
            mergeNode.next = mergeTwoLists(l1.next, l2);
        }
        return mergeNode;
    }

    // 判断单链表成环与否？
    public boolean hasCycle(ListNode head) {
        Set<ListNode> listSet = new HashSet<>();

        while (head != null) {
            if (listSet.contains(head))
                return true;
            else
                listSet.add(head);

            head = head.next;
        }

        return false;
    }

    // 链表翻转（即：翻转一个单项链表）
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode temp = head.next;

            head.next = prev;
            prev = head;
            head = temp;
        }

        return prev;
    }
}
