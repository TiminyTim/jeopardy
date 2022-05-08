COMP = javac
FLGS =#--module-path
LIBS =

all: Client.class Server.class

Client.class: Client.java
	$(COMP) $(LIBS) Client.java $(FLGS)

Server.class: Server.java
	$(COMP) $(LIBS) Server.java $(FLGS)

clean:
	rm -rf *.class
