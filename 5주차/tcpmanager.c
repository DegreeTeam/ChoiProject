#include <sys/types.h>
#include<stdio.h>
#include<string.h>    //strlen
#include<stdlib.h>    //strlen
#include<sys/socket.h>
#include <arpa/inet.h> //inet_addr
#include <unistd.h>    //write
#include <netinet/in.h>
#include <signal.h>
#include <errno.h>

#define SIZE 32

int main(int argc , char *argv[])
{
    int socket_desc , client_sock, c, n;
    struct sockaddr_in server , client;
    char rcvBuffer[SIZE];

    //Prepare the sockaddr_in structure
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = htonl(INADDR_ANY);
    server.sin_port = htons( 2008 );

    //Create socket
    if ((socket_desc = socket(AF_INET , SOCK_STREAM , 0)) == -1)
    {
        printf("Could not create socket");
    }

    //Bind
    if (bind(socket_desc,(struct sockaddr *)&server , sizeof(server)) < 0)
    {
        //print the error message
        perror("bind failed. Error");
        return 1;
    }
    puts("bind done");

    //Listen
    listen(socket_desc , 3);
    //Accept and incoming connection
    puts("Waiting for incoming connections...");
    c = sizeof(struct sockaddr_in);

    while(1){

	  if(client_sock = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&c))
        { 
		printf("accept success \n");
	        while((n = read(client_sock, rcvBuffer, sizeof(rcvBuffer))))

			{
				 rcvBuffer[n] = '\0';
				 printf("%s", rcvBuffer);
			}
			printf("%s", rcvBuffer);
			close(client_sock);
        }
  
	}
	close(socket_desc);
    return 0;
}






