import java.util.Random;
import java.util.Scanner;

public class Optimal {
	public static void main(String[] args) 
	{
		//Initialize Variables
		int noOfPageFrames;
		int noOfAddresses = 20;
		int pageSize = 4096;
		int pageFaultCount = 0;
		BeladyPage[] frames;
		byte[] addressPageNumbers = new byte[noOfAddresses];

		
		//ask for and validate user input
		Scanner keyboard = new Scanner(System.in);
		System.out.println("How many page frames are available for the process? (2-7)");
		noOfPageFrames = keyboard.nextInt();
		while(noOfPageFrames < 2 || noOfPageFrames > 7)
		{
			System.out.println("Error: Please enter a number between 2 and 7");
			noOfPageFrames = keyboard.nextInt();
		}
		keyboard.close();

		// Initialize the array for frames
		// since max possible page number is 15, use 16 for empty validation
		frames = new BeladyPage[noOfPageFrames];
		for(int i = 0; i < frames.length; i++)
		{
			BeladyPage page = new BeladyPage((byte)16);
			frames[i] = page;
		}

		// Generate a sequence of 100 random memory addresses from a 16-bit address space.
		// Use your address translation algorithm to determine address’s page.
		for(int i = 0; i < noOfAddresses; i++)
		{
			Random randomNum = new Random();
			addressPageNumbers[i] = (byte) (randomNum.nextInt(Short.MAX_VALUE + 1) / pageSize);
			System.out.print(addressPageNumbers[i] + " - ");
		}
		System.out.print("\n");

		// now we have:
		// addressPageNumbers - an array of 100 page numbers between 0 and 15
		// frames - an array of 2-7 frames which can hold objects, each one remembering the page number in it,
		// 			the number of steps to get to that number again (or 101 if not used)
		//			the position when it was first used if we need to default to FIFO
		// pageFaultCount - a counter to keep track of page faults
		
		
		// loop through each of the 100 page numbers
		for (int i = 0; i < addressPageNumbers.length; i++)
		{
			// gate for if space is found
			boolean foundSpace = false;
			
			// check if number is already there
			for (int j = 0; j < frames.length; j++)
			{
				if (frames[j].pageNumber == addressPageNumbers[i])
				{
					foundSpace = true;
					break;
				}
			}
			
			// if space not found, check for 16, which means unused frame. increment page fault
			if (!foundSpace)
			{
				for (int j = 0; j < frames.length; j++)
				{
					if (frames[j].pageNumber == 16)
					{
						frames[j].pageNumber = addressPageNumbers[i];
						
						frames[j].positionFirstUsed = i;
						pageFaultCount++;
						foundSpace = true;
						break;
					}
				}
			}
			
			// if space still not found, compare frames to find longest lengthToNext. increment page fault
			if (!foundSpace)
			{
				int frameToReplace = 0;
				int longestLengthToNext = -1;
				int countOfLongest = 0;
				
				// find longest length to next
				for (int j = 0; j < frames.length; j++)
				{
					if (frames[j].lengthToNext > longestLengthToNext)
					{
						longestLengthToNext = frames[j].lengthToNext;
						frameToReplace = j;
					}
				}
				
				// for longest, check if more than one
				for (int j = 0; j < frames.length; j++)
				{
					if (frames[j].lengthToNext == longestLengthToNext)
					{
						countOfLongest++;
					}
				}
				
				// if more than one longest number, check FIFO
				if (countOfLongest > 1)
				{
					int earliestPosition = 101;
					for (int j = 0; j < frames.length; j++)
					{
						if (frames[j].positionFirstUsed < earliestPosition)
						{
							earliestPosition = frames[j].positionFirstUsed;
							frameToReplace = j;
						}
					}
					frames[frameToReplace].pageNumber = addressPageNumbers[i];
					frames[frameToReplace].positionFirstUsed = i;
					foundSpace = true;
				}
				
				//if only one, use it
				else
				{
					frames[frameToReplace].pageNumber = addressPageNumbers[i];
					frames[frameToReplace].positionFirstUsed = i;
					foundSpace = true;
				}
				
				pageFaultCount++;
			}
			
			// after space is found, output and update all frames to new length to next 
			outPutFrames(frames);
			for (int j = 0; j < frames.length; j++)
			{
				frames[j].lengthToNext = calculateLength(frames[j].pageNumber, addressPageNumbers, i);
			}
			
		}
		
		System.out.println("Page faults: " + pageFaultCount);
		
		
		

		

	} // end main
	
	//need function to print current page contents
	
	public static void outPutFrames(BeladyPage[] frames)
	{
		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i].pageNumber == 16)
			{
				System.out.println("Frame " + i + ": X | ");
			}
			else
			{
				System.out.println("Frame " + i + ": " + frames[i].pageNumber + " | ");
			}
		}
		System.out.println("\n");
	}

	// function to check distance to next occurrence
	// if no occurrence, returns -1
	public static int calculateLength(int page, byte[] list, int position)
	{
		int length = 101;
		for (int i = position + 1; i < list.length; i++)
		{
			if (page == list[i])
			{
				length = i - position;
				break;
			}
		}
		return length;
	}

}

class BeladyPage
{
	byte pageNumber;
	int lengthToNext;
	int positionFirstUsed;

	// Constructor
	BeladyPage(byte pageNumber)
	{
		this.pageNumber = pageNumber;
		this.lengthToNext = -1;
		positionFirstUsed = 101;
	}
}
