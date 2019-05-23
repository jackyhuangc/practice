package com.jacky.practice;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;

import javax.swing.tree.TreeNode;

/**
 * Description Here!
 * 
 * @author Jacky Huang
 * @date 2018-02-02 11:53
 * @since jdk1.8
 */
public class LeetCode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// 4. Median of Two Sorted Arrays
		Solution s = new Solution();
		int[] nums1 = new int[] { 1, 2, 3 };
		int[] nums2 = new int[] { 5, 2 };
		// double r = s.findMedianSortedArrays(nums1, nums2);
		int r = s.numJewelsInStones("abce", "bcda");
		System.out.println(r);

		String shortUrl = s.encode("http://www.baidu.com/");
		String longUrl = s.decode(shortUrl);
		System.out.println(longUrl);

		// System.out.println(s.hammingDistance(5, 9));
		// System.out.println(s.titleToNumber("SS"));
		// System.out.println(s.rob(new int[] { 1, 2, 3, 4, 5, 6 }));

		int ret = s.removeDuplicates(new int[] { 1, 1, 2, 3, 9, 9 });

		Stack<Character> stack = new Stack<Character>();
		stack.push('}');

		URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
		for (int i = 0; i < urls.length; i++) {
			System.out.println(urls[i].toExternalForm());
		}

		System.out.println("abcdef".substring(2, 3));
		s.merge(new int[] { 7, 8, 9, 10 }, 3, new int[] { 5 }, 1);
		System.out.println(1 & 2);

		ClassLoader classLoader = Solution.class.getClassLoader();
		while (classLoader != null) {
			System.out.println(classLoader);
			classLoader = classLoader.getParent();
		}
		System.out.println(classLoader);

		TestHash t1 = new TestHash("a", "1");
		TestHash t2 = new TestHash("a", "2");
		if (t1 == t2) {
			System.out.println("xxx");
		}

		if (t1.equals(t2)) {
			System.out.println("yyy");
		}

		Comparator comparator = new Comparator() {
			@Override
			public int compare(Object object1, Object object2) {
				
				return 0;
			}
		};
	}
}

class TestHash {

	private String name;

	public String getName() {
		return name;
	}

	TestHash() {
	}

	TestHash(String name, String age) {

		this.name = name;
		this.age = age;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	private String age;

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		return this.name == ((TestHash) obj).getName();
	}

}

// Ĭ����default���ԣ�package�ڿɷ���
class Solution {

	public void merge(int A[], int m, int B[], int n) {
		int i = m - 1, j = n - 1, k = m + n - 1;

		while (i >= 0 && j >= 0) {
			A[k--] = (A[i] > B[j]) ? A[i--] : B[j--];
		}

		while (j >= 0)
			A[k--] = B[j--];
	}

	public int removeDuplicates(int[] nums) {
		if (nums.length == 0)
			return 0;
		int i = 0;
		for (int j = 1; j < nums.length; j++) {
			if (nums[j] != nums[i]) {
				i++;
				nums[i] = nums[j];
			}
		}
		return i + 1;
	}

	public int[] plusOne(int[] digits) {

		int n = digits.length;
		for (int i = n - 1; i >= 0; i--) {
			if (digits[i] < 9) {
				digits[i]++;
				return digits;
			}

			digits[i] = 0;
		}

		int[] newNumber = new int[n + 1];
		newNumber[0] = 1;

		return newNumber;
	}

	public int rob(int[] num) {
		System.out.println("***********************");
		int prevNo = 0;
		int prevYes = 0;
		for (int n : num) {
			int temp = prevNo;
			System.out.println(temp);
			prevNo = Math.max(prevNo, prevYes);
			prevYes = n + temp;
		}

		return Math.max(prevNo, prevYes);
	}

	public int climbStairs(int n) {
		int memo[] = new int[n + 1];
		return climb_Stairs(0, n, memo);
	}

	private int climb_Stairs(int i, int n, int memo[]) {
		if (i > n) {
			return 0;
		}
		if (i == n) {
			return 1;
		}
		if (memo[i] > 0) {
			return memo[i];
		}
		memo[i] = climb_Stairs(i + 1, n, memo) + climb_Stairs(i + 2, n, memo);
		return memo[i];
	}

	public int maxProfit(int[] prices) {
		int maxProfit = 0;
		for (int i = 0; i < prices.length - 1; i++) {
			for (int j = i + 1; j < prices.length; j++) {
				int profit = prices[j] - prices[i];
				if (profit > maxProfit) {
					maxProfit = profit;
				}
			}
		}

		return maxProfit;
	}

	public int missingNumber(int[] nums) {
		Arrays.sort(nums);

		if (nums[nums.length - 1] != nums.length) {
			return nums.length;
		} else if (nums[0] != 0) {
			return 0;
		}

		for (int i = 1; i < nums.length; i++) {
			int expectedNum = nums[i - 1] + 1;
			if (nums[i] != expectedNum) {
				return expectedNum;
			}
		}

		return -1;
	}

	public double findMedianSortedArrays(int[] A, int[] B) {
		int m = A.length;
		int n = B.length;
		if (m > n) { // to ensure m<=n
			int[] temp = A;
			A = B;
			B = temp;
			int tmp = m;
			m = n;
			n = tmp;
		}
		int iMin = 0, iMax = m, halfLen = (m + n + 1) / 2;
		while (iMin <= iMax) {
			int i = (iMin + iMax) / 2;
			int j = halfLen - i;
			if (i < iMax && B[j - 1] > A[i]) {
				iMin = iMin + 1; // i is too small
			} else if (i > iMin && A[i - 1] > B[j]) {
				iMax = iMax - 1; // i is too big
			} else { // i is perfect
				int maxLeft = 0;
				if (i == 0) {
					maxLeft = B[j - 1];
				} else if (j == 0) {
					maxLeft = A[i - 1];
				} else {
					maxLeft = Math.max(A[i - 1], B[j - 1]);
				}
				if ((m + n) % 2 == 1) {
					return maxLeft;
				}

				int minRight = 0;
				if (i == m) {
					minRight = B[j];
				} else if (j == n) {
					minRight = A[i];
				} else {
					minRight = Math.min(B[j], A[i]);
				}

				return (maxLeft + minRight) / 2.0;
			}
		}
		return 0.0;
	}

	// 771. Jewels and Stones
	public int numJewelsInStones(String J, String S) {
		int res = 0;
		Set setJ = new HashSet();
		for (char j : J.toCharArray())
			setJ.add(j);
		for (char s : S.toCharArray())
			if (setJ.contains(s))
				res++;
		return res;
	}

	Map<String, String> index = new HashMap<String, String>();
	Map<String, String> history = new HashMap<String, String>();
	String BASE_URL = "http://tinyurl.com/";

	// Encodes a URL to a shortened URL.
	public String encode(String longUrl) {

		if (history.containsKey(longUrl))
			return BASE_URL + history.get(longUrl);
		String charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

		String key = "";
		do {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 6; i++) {
				int r = (int) (Math.random() * charSet.length());
				sb.append(charSet.charAt(r));
			}

			key = sb.toString();
		} while (index.containsKey(key));

		index.put(key, longUrl);
		history.put(longUrl, key);

		return BASE_URL + key;
	}

	// Decodes a shortened URL to its original URL.
	public String decode(String shortUrl) {

		String key = shortUrl.replace(BASE_URL, "");

		return index.get(key);
	}

	/*
	 * public TreeNode constructMaximumBinaryTree(int[] nums) { if (nums ==
	 * null) return null;
	 * 
	 * return build(nums, 0, nums.length - 1); }
	 * 
	 * private TreeNode build(int[] nums, int start, int end) { if (start > end)
	 * return null;
	 * 
	 * int idxMax = start; for (int i = start + 1; i <= end; i++) { if (nums[i]
	 * > nums[idxMax]) { idxMax = i; } }
	 * 
	 * TreeNode root = new TreeNode(nums[idxMax]);
	 * 
	 * root.left = build(nums, start, idxMax - 1); root.right = build(nums,
	 * idxMax + 1, end);
	 * 
	 * return root; }
	 */

	public int hammingDistance(int x, int y) {
		// return Integer.bitCount(x ^ y);

		int xor = x ^ y;
		int res = 0;
		while (xor != 0) {
			res += xor & 1;
			xor >>= 1;
		}
		return res;
	}

	public int titleToNumber(String s) {
		int result = 0;
		for (int i = 0; i < s.length(); i++) {
			result = result * 26 + (s.charAt(i) - 'A' + 1);
		}
		return result;
	}

	public int majorityElement(int[] nums) {

		/*
		 * int majorityCount = nums.length / 2;
		 * 
		 * for (int num : nums) { int count = 0; for (int elem : nums) { if
		 * (elem == num) { count += 1; } }
		 * 
		 * if (count > majorityCount) { return num; }
		 * 
		 * }
		 * 
		 * return -1;
		 */
		int count = 0, ret = 0;
		for (int num : nums) {
			if (count == 0)
				ret = num;
			if (num != ret)
				count--;
			else
				count++;
		}

		if (count > 0) {
			return ret;
		}
		return -1;
	}

	public boolean containsDuplicate(int[] nums) {
		for (int i = 0; i < nums.length; ++i) {
			for (int j = 0; j < nums.length && j != i; ++j) {
				if (nums[j] == nums[i])
					return true;
			}
		}
		return false;
	}
}