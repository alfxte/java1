package ns.blank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

class Syllablelizer {
	public static HashSet<Character> sVocals = new HashSet<>();
	public static HashSet<Character> sConsonant = new HashSet<>();
	
	static {			
		sVocals.addAll(Arrays.asList('a', 'i', 'u', 'e', 'o'));
		
		sConsonant.addAll(Arrays.asList(
					'b', 'c', 'd', 'f', 'g',
					'h', 'j', 'k', 'l', 'm',
					'n', 'p', 'q', 'r', 's',
					't', 'v', 'w', 'x', 'y','z'						
				));
	}
	
	public static boolean isVocal(char ch) {
		return sVocals.contains(ch);
	}
	
	public static boolean isConsonant(char ch) {
		return sConsonant.contains(ch);
	}
	
	
	public static boolean isConsonantOrException(char[] chars, int idx) {
		try  {
			return isConsonant(chars[idx]);
		} catch (Exception e) {
			return true;
		}
	}
	
	public static boolean isSingleConsonantTrailingOrException(char[] chars, int idx) {
		try  {
			return isConsonant(chars[idx]) && consonantCount(chars, idx) == 1;
		} catch (Exception e) {
			return true;
		}
	}
	
	public static int consonantCount(char[] chars, int offset) {
		int count = 0;
		int size = chars.length;
		for (; offset < size; offset++) {
			if (isConsonant(chars[offset])) {
				count++;
			}
		}
		return count;
	}
	
	public static String[] toSyllable(String str) {
		
		List<String> retval = new ArrayList<>();
		while(!str.isEmpty()) {
			//System.out.println("#> " + str);
			char[] chars = str.toCharArray();
			
			try {		
			
				// syllable V[?]
				if (isVocal(chars[0])) {
					
					// syllable V[C]
					try {
						if (isConsonant(chars[1]) && isConsonantOrException(chars, 2)) {
							retval.add(new String(chars, 0, 2));
							str = str.substring(2, str.length());	
						}
						
					// syllable V[-]
						else {
							retval.add(new String(chars, 0, 1));
							str = str.substring(1, str.length());
						}
					}										
					catch (Exception e) {
						retval.add(new String(chars, 0, 1));
						str = str.substring(1, str.length());
					}					
				} 
				
				// syllable C[?][?][?][?]
				else {
					
					// syllable CV[?][?][-]
					if (isVocal(chars[1])) {						
						
						// syllable CV[C][?][-]
						try {
							if (isConsonant(chars[2]) && isConsonantOrException(chars, 3)) {								
								// syllable CV[C][C][-]								
								try {
									if (isSingleConsonantTrailingOrException(chars, 4)) {										
										retval.add(new String(chars, 0, 4));										
										str = str.substring(4, str.length());
									}
									
								// syllable CV[C][-][-]
									else {
										retval.add(new String(chars, 0, 3));
										str = str.substring(3, str.length());
									}
									
								}catch (Exception e) {
									retval.add(new String(chars, 0, 3));
									str = str.substring(3, str.length());
								}								
								
							}													
						
						// syllable CV[-][-][-]
							else {
								retval.add(new String(chars, 0, 2));
								str = str.substring(2, str.length());
							}
						}
						catch (Exception e) {						
							retval.add(new String(chars, 0, 2));
							str = str.substring(2, str.length());
						}
						
					}	
						
					// syllable CC[?][?][?]
					else {
						
						// syllable CC[V][?][?]
						if (isVocal(chars[2])) {
							
							// syllable CCV[C][-]
							try {
								if (isConsonant(chars[3])) {
									if (isConsonantOrException(chars, 4)) {
										retval.add(new String(chars, 0, 4));
										str = str.substring(4, str.length());
									} else if (isSingleConsonantTrailingOrException(chars, 4)) {
										retval.add(new String(chars, 0, 4));
										str = str.substring(4, str.length());
									}									
								} 							
							
							// syllable CCV[-][-]
								else {
									retval.add(new String(chars, 0, 3));
									str = str.substring(3, str.length());
								}
							}
							catch (Exception e) {
								retval.add(new String(chars, 0, 3));
								str = str.substring(3, str.length());
							}							
						} 
						
						// syllable CC[C][?][?]
						else {
	
							// syllable CCC[V][?]
							if (isVocal(chars[3])) {
	
								// syllable CCCV[C]
								try {
									if (isConsonant(chars[4]) && isConsonantOrException(chars, 5)) {
										retval.add(new String(chars, 0, 5));
										str = str.substring(5, str.length());	
									}
									
								// syllable CCCV[-]
									else {
										retval.add(new String(chars, 0, 4));
										str = str.substring(4, str.length());
									}
								}										
								catch (Exception e) {
									retval.add(new String(chars, 0, 4));
									str = str.substring(4, str.length());
								}
								
							}
							
						}
					}
				}		
			} catch (Exception e) {
				// if reached here, it means the text unable to parsed according to syllable.
				// and will redo the word spelling as monophone. (ask the system to apply rule 1 instead)
				return null;
			}
		}
					
		// means the word successfully syllablelized
		return retval.toArray(new String[0]);
	}
}
