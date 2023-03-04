# UNO
A simple implimention of UNO board game

At first , the number of players before the start of the game is determined by the user .
one of them will be the user himself and the other players will be the computer .
At the beginning of the game , each player is randomly dealt 7 cards .
After that , one card is randomly announced as the starting card from among the remaining cards . And the rest of the cards are stored in the card tank .
In the following , one of players is randomly announced as the starter .
The game is played in one round and after the completion of the round the score of the players is displayed .

![image](https://user-images.githubusercontent.com/99418863/222918182-9a6b71e4-bb54-41eb-8754-cc75f5147cd1.png)

The server and client of this game are written by socket programming and include parts such as :

1. Authentication by the server (registration or login with an existing account)

2. A logger on the server that logs all events

![image](https://user-images.githubusercontent.com/99418863/222919429-619d17f8-9151-4c9f-b15f-a0c4a9234632.png)

Also , due to the multi-threaded nature of the program , many people can play their game independently without any problems
