import type { AIModel, Message, User } from "../types";

class ApiService {
  private baseUrl = import.meta.env.VITE_MICRO_GATEWAY_URL;
  private token: string | null = null;

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
      "Content-Type": "application/json",
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

  async getUsers(): Promise<User[]> {
    return this.request<User[]>("/user-service/users");
  }

  async getAIModel(): Promise<AIModel[]> {
    return this.request<AIModel[]>("/ai-service/ais");
  }

  async getChats(): Promise<Message[]> {
    return this.request<Message[]>("/chat-service/chats");
  }
 
  async ensureUser(user: Pick<User, "idUser" | "username">): Promise<User> {
    return this.request<User>("/user-service/users/ensure", {
      method: "POST",
      body: JSON.stringify(user),
    });
  }

  async getMessagesWith(userId: string, userId2: string): Promise<Message[]> {
    return this.request<Message[]>(
      `/chat-service/chats/conv?u1=${userId}&u2=${userId2}`
    );
  }

  async sendMessage(
    senderUserId: string,
    receiverUserId: string,
    content: string,
    aiId: number | null = null
  ): Promise<Message> {
    return this.request<Message>("/chat-service/chats", {
      method: "POST",
      body: JSON.stringify({
        senderUserId,
        receiverUserId,
        aiId,
        chatcontent: content,
        created_at: new Date().toISOString(),
      }),
    });
  }
}

export const apiService = new ApiService();
export default apiService;
