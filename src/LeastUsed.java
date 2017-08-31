import java.util.Random;
import java.util.Scanner;

public class LeastUsed {
	public static void main(String[] args) 
	{
		//Initialize Variables
		int frameCount= 0;
		int pageFaultCount = 0; 
		int lastOpenFrame;
		int addressCount = 100;
		int pageSize = 4096;
		int lowestPageCount;
		int firstPageIn;
		Page[] frames;
		byte[] pageNumbers = new byte[addressCount];
		boolean pageFaultCheck;
		boolean openFrameCheck;
		
		// initialize input
		Scanner keyboard = new Scanner(System.in);
		
		//ask for and validate user input
		System.out.println("Enter number of frames (between 2 and 7)");
		frameCount = keyboard.nextInt();
		while(frameCount < 2 || frameCount > 7)
		{
			System.out.println("Must be between 2 and 7");
			frameCount = keyboard.nextInt();
		}

		// Initialize the array for frames
		// since max possible page number is 15, use 16 for validation
		frames = new Page[frameCount];
		for(int i = 0; i < frames.length; i++)
		{
			Page page = new Page((byte)16);
			frames[i] = page;
		}


		// Generate a sequence of 100 random memory addresses from a 16-bit address space.
		// Use your address translation algorithm to determine address’s page.
		for(int i = 0; i < addressCount; i++)
		{
			Random randomNum = new Random();
			pageNumbers[i] = (byte) (randomNum.nextInt(Short.MAX_VALUE + 1) / pageSize);
			System.out.print(pageNumbers[i] + " - ");
		}
		System.out.print("\n");
		
		

		// Run through page number list, checking for page faults and assigning addresses, then output result
		for(int i = 0; i < pageNumbers.length; i++)
		{
			pageFaultCheck = true;
			for(int j = 0; j < frames.length; j++){
				if(frames[j].pageNumber == pageNumbers[i])
				{
					pageFaultCheck = false;
					frames[j].count++;
				}
			}
			// check each space for open space
			if(pageFaultCheck)
			{
				openFrameCheck = false;
				lastOpenFrame = 0;
				for(int j = 0; j < frames.length; j++)
				{
					if(frames[j].pageNumber == 16 && !openFrameCheck)
					{
						openFrameCheck = true;
						lastOpenFrame = j;
					}
				}
				// if open, use that space
				if(openFrameCheck)
				{
					frames[lastOpenFrame].pageNumber = pageNumbers[i];
					frames[lastOpenFrame].count++;
					frames[lastOpenFrame].lastInTime = i;
				}
				//if not, check for page used least number of times
				else
				{
					lowestPageCount = addressCount + 1;
					firstPageIn = addressCount + 1;
					for(int j = 0; j < frames.length; j++)
					{
						if(frames[j].count < lowestPageCount)
						{
							lowestPageCount = frames[j].count;
						}

						if(frames[j].count == lowestPageCount && frames[j].lastInTime < firstPageIn)
						{
							firstPageIn = frames[j].lastInTime;
						}
					}
					// set info for next loop
					for(int j = 0; j < frames.length; j++)
					{
						if(frames[j].lastInTime == firstPageIn)
						{
							frames[j].pageNumber = pageNumbers[i];
							frames[j].count = 1;
							frames[j].lastInTime = i;
						}
					}
				}
				pageFaultCount++;
			}
			System.out.print((i + 1) +" => ");

			// console output
			// Display the progress of your algorithms and final results for the page-references string. 
			for(int j = 0; j < frames.length; j++)
			{
				if(frames[j].pageNumber != 16) 
				{
					System.out.print("Frame " + j + ": " + frames[j].pageNumber);
				}
				else
				{
					System.out.print(j + ":  ");
				}
				if( j != frames.length - 1)
				{
					System.out.print(" | ");
				}
			}
			System.out.print('\n');
		}
		System.out.println("Number of Page Faults: " + pageFaultCount);
		keyboard.close();
	}
}

class Page
{
	byte pageNumber;
	int count;
	int lastInTime;

	//Constructor to initialize a new page
	Page(byte pageNumber)
	{
		this.pageNumber = pageNumber;
		this.count = 0;
		this.lastInTime = 0;
	}
}