export interface User {
  idUser: string;
  username: string; 
  email: string; 
  firstName: string; 
  lastName: string; 
}

export interface SyncUser {
  idUser: string;
  username: string;  
}

export interface Message {
  idChat: number;
  chatcontent: string;
  senderUserId: string;
  receiverUserId: string;
  aiId: number | null;
  created_at: string;
  chatmaj: Boolean;
  
    sender?: {
        idUser: string;
        username: string;
    };
    receiver?: {
        idUser: string;
        username: string;
    };
    ai?: {
        idAi: string;
        libelle: string;
        is_active: Boolean;
    }
}

export interface MessageBot {
  idChat: string;
  content: string;
  sender: string;
  receiver?: string;
  the_sender?: Boolean;
  timestamp: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface AIModel {
  idAi: number;
  aiId: string;
  libelle: string;
}