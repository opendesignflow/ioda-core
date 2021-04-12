//typedef void * ViHndlr;

//#include "../jnaerator/visa.h"
#define NIVISA_USB
#include <visa.h>
#include "stdio.h"



int main() {

    printf("Hello\n");

    ViStatus status;
    printf("Sizeof Status: %d\n",sizeof(ViStatus));
    ViSession defaultRM = VI_NULL;
    //unsigned long defaultRM = 0;
    status=viOpenDefaultRM(&defaultRM);

    printf("Default RM: %d\n",defaultRM);

    ViUInt32 numInstrs;
   ViFindList findList;
    char instrResourceString[VI_FIND_BUFLEN];
   status = viFindRsrc(defaultRM, (ViString)"USB?*INSTR", &findList, &numInstrs,
                       instrResourceString);

   if (status < VI_SUCCESS)
   {
      printf("An error occurred while finding resources.\nHit enter to continue.");
      status = viStatusDesc(defaultRM,status,instrResourceString);
      printf("Status error result: %d\n",status );
      //fflush(stdin);
      //getchar();
      
      //return(status);
   }

   printf("Found instruments: %d \n",numInstrs);

   viClose(defaultRM);

    return 0;
}
