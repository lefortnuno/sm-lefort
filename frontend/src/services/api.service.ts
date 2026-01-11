import type { AuthResponse, Message, User } from '../types';

class ApiService {
  private baseUrl = 'http://localhost:8085';
  private wsUrl = 'ws://localhost:8085/chat-service/ws/chat';
  private token: string | null = null;
  private socket: WebSocket | null = null;

  setToken(token: string) {
    this.token = token;
  }

  getToken(): string | null {
    return this.token;
  }

  clearToken() {
    this.token = null;
  }

  private async request<T>(
    endpoint: string, 
    options: RequestInit = {}
  ): Promise<T> {
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
      ...(this.token && { Authorization: `Bearer ${this.token}` }),
      ...options.headers,
    };

    const response = await fetch(`${this.baseUrl}${endpoint}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      throw new Error(`API Error: ${response.status}`);
    }

    return response.json();
  }
 

  // Users endpoints a changer par keyclock
  async getUsers(): Promise<User[]> {  
    return this.request<User[]>('/user-service/users'); 
  }

  async ensureUser(user: Pick<User, 'idUser' | 'username'>): Promise<User> {
    return this.request<User>('/user-service/users/ensure', {
      method: 'POST',
      body: JSON.stringify(user),
    });
  }

  // Messages endpoints
  async getMessagesWith(userId: string, userId2: string): Promise<Message[]> { 
    return this.request<Message[]>(`/chat-service/conv?u1=${userId}&u2=${userId2}`);
 
  }

  async sendMessage(senderUserId: string, receiverUserId: string, content: string, aiId: number | null = null): Promise<Message> { 
    return this.request<Message>('/chat-service/chats', {
      method: 'POST',
      body: JSON.stringify({ 
        senderUserId,
        receiverUserId,
        aiId,
        chatcontent: content,
        created_at: new Date().toISOString()
     }),
    });
 
  }
  // WebSocket Methods
  connectWS(onMessage: (data: string) => void) {
    if (this.socket && (this.socket.readyState === WebSocket.OPEN || this.socket.readyState === WebSocket.CONNECTING)) {
      return; // Déjà connecté ou en cours
    }

    console.log('Connecting to WebSocket...');
    this.socket = new WebSocket(this.wsUrl);

    this.socket.onopen = () => {
      console.log('✅ WS Connected');
    };

    this.socket.onmessage = (event) => {
      onMessage(event.data);
    };

    this.socket.onerror = (error) => {
      console.error('❌ WS Error:', error);
    };

    this.socket.onclose = () => {
      console.log('⚠️ WS Disconnected');
      this.socket = null;
    };
  }

  disconnectWS() {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }

  sendWS(message: string) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(message);
    } else {
      console.warn('Cannot send message: WS is not open');
    }
  }
}

export const apiService = new ApiService();
export default apiService;