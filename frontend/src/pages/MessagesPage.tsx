import { useState, useEffect, useRef } from "react";
import {
  MessageCircle,
  Send,
  LogOut,
  Loader2,
  Brain,
  ChevronDown,
} from "lucide-react";
import type { User, Message, AIModel } from "../types";
import apiService from "../services/api.service";
import { UsersSidebar } from "../components/UsersSidebar";
import { LogoutModal } from "../components/LogoutModal";

interface MessagesPageProps {
  user: User;
  onLogout: () => void;
}

// ===== COMPOSANT SÉLECTEUR DE MODÈLE AI =====
const AIModelSelector = ({
  models,
  selectedModelId,
  onSelectModel,
  loading,
}: {
  models: AIModel[];
  selectedModelId: number;
  onSelectModel: (id: number) => void;
  loading: boolean;
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const selectedModel = models.find((m) => m.idAi === selectedModelId);

  return (
    <div className="relative">
      <button
        onClick={() => setIsOpen(!isOpen)}
        disabled={loading}
        className="flex items-center gap-2 px-3 py-2 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition disabled:opacity-50"
      >
        <Brain className="w-4 h-4 text-purple-600" />
        <span className="text-sm font-medium text-gray-700">
          {loading ? "Chargement..." : selectedModel?.libelle || "Sélectionner"}
        </span>
        <ChevronDown
          className={`w-4 h-4 text-gray-400 transition-transform ${
            isOpen ? "rotate-180" : ""
          }`}
        />
      </button>

      {isOpen && !loading && (
        <>
          <div
            className="fixed inset-0 z-10"
            onClick={() => setIsOpen(false)}
          />
          <div className="absolute top-full mt-2 right-0 bg-white border border-gray-200 rounded-lg shadow-lg z-20 min-w-[200px]">
            <div className="py-1">
              {models.map((model) => (
                <button
                  key={model.idAi}
                  onClick={() => {
                    onSelectModel(model.idAi);
                    setIsOpen(false);
                  }}
                  className={`w-full text-left px-4 py-2 text-sm hover:bg-gray-50 transition flex items-center gap-2 ${
                    model.idAi === selectedModelId
                      ? "bg-purple-50 text-purple-700"
                      : "text-gray-700"
                  }`}
                >
                  <Brain className="w-4 h-4" />
                  {model.libelle}
                  {model.idAi === selectedModelId && (
                    <span className="ml-auto text-xs">✓</span>
                  )}
                </button>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
};

// ===== COMPOSANT PRINCIPAL MessagesPage =====
export const MessagesPage = ({ user, onLogout }: MessagesPageProps) => {
  const [users, setUsers] = useState<User[]>([]);
  const [aimodels, setAimodels] = useState<AIModel[]>([]);
  const [selectedUserId, setSelectedUserId] = useState<string | null>(null);
  const [selectedAIModelId, setSelectedAIModelId] = useState<number>(1);
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState("");
  const [loadingUsers, setLoadingUsers] = useState(true);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [loadingAIModels, setLoadingAIModels] = useState(false); 
  const [sending, setSending] = useState(false);
  const [showLogoutModal, setShowLogoutModal] = useState(false); 
  const selectedUserIdRef = useRef(selectedUserId);
  const selectedAimodelIdRef = useRef(selectedAIModelId);

  useEffect(() => {
    selectedUserIdRef.current = selectedUserId;
  }, [selectedUserId]);

  useEffect(() => {
    selectedAimodelIdRef.current = selectedAIModelId;
  }, [selectedAIModelId]);

  useEffect(() => {
    loadUsers();
    loadAIModels();
  }, []);

  // Charger les messages quand un utilisateur est sélectionné
  useEffect(() => {
    if (selectedUserId) {
      loadMessages(selectedUserId, user.idUser);
    }
  }, [selectedUserId]);

  const loadUsers = async () => {
    try {
      const data = await apiService.getUsers();
      setUsers(data);
    } catch (error) {
      console.error("Failed to load users:", error);
    } finally {
      setLoadingUsers(false);
    }
  };

  // USER CHECKER TROFEL
  useEffect(() => {
    const checkNewUsers = async () => {
      try {
        const data = await apiService.getUsers();

        if (data.length !== users.length) {
          setUsers(data);
        }
      } catch (error) {
        console.error(
          "Erreur lors de la vérification des utilisateurs:",
          error
        );
      }
    };

    const intervalUsers = setInterval(checkNewUsers, 9000);
    return () => clearInterval(intervalUsers);
  }, [users]);

  // MESSAGE CHECKER TROFEL
  const lastMessageRef = useRef<{
    idChat: number | null;
    updatedAt: string | null;
  }>({ idChat: null, updatedAt: null });

  useEffect(() => {
    if (!selectedUserId) return;

    const checkNewMessages = async () => {
      try {
        const data = await apiService.getChats();

        const relevantMessages = data.filter(
          (msg) =>
            (msg.senderUserId === user.idUser &&
              msg.receiverUserId === selectedUserId) ||
            (msg.senderUserId === selectedUserId &&
              msg.receiverUserId === user.idUser)
        );

        if (relevantMessages.length === 0) return;

        const lastMessage = relevantMessages[relevantMessages.length - 1];

        const hasNewMessage =
          lastMessage.idChat !== lastMessageRef.current.idChat;

        const hasUpdate =
          lastMessage.chatmaj === true &&
          lastMessage.updated_at !== lastMessageRef.current.updatedAt;

        if (hasNewMessage || hasUpdate) {
          lastMessageRef.current = {
            idChat: lastMessage.idChat,
            updatedAt: lastMessage.updated_at,
          };

          loadMessages(selectedUserId, user.idUser);
        }
      } catch (error) {
        console.error("Erreur lors de la vérification des messages:", error);
      }
    };

    const intervalMessages = setInterval(checkNewMessages, 5000);
    return () => clearInterval(intervalMessages);
  }, [selectedUserId, user.idUser]);

  // NOUVELLE FONCTION : Charger les modèles AI
  const loadAIModels = async () => {
    setLoadingAIModels(true);
    try {
      const data = await apiService.getAIModel();
      setAimodels(data);
    } catch (error) {
      console.error("Failed to load AI models:", error);
    } finally {
      setLoadingAIModels(false);
    }
  };

  const loadMessages = async (userId: string, userId2: string) => {
    setLoadingMessages(true);
    try {
      const data = await apiService.getMessagesWith(userId, userId2);
      setMessages(data);
    } catch (error) {
      console.error("Failed to load messages:", error);
    } finally {
      setLoadingMessages(false);
    }
  };

  const handleSend = async () => {
    if (!newMessage.trim() || sending || !selectedUserId) return;

    setSending(true);
    try {
      const message = await apiService.sendMessage(
        user.idUser,
        selectedUserId,
        newMessage,
        selectedAimodelIdRef.current
      );
      setMessages((prev) => [...prev, message]);

      setNewMessage("");
    } catch (error) {
      console.error("Failed to send message:", error);
    } finally {
      setSending(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey && !sending && newMessage.trim()) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleLogout = () => {
    setShowLogoutModal(false);
    onLogout();
  };

  const selectedUser = users.find((u) => u.idUser === selectedUserId);

  const getAvatarColor = (username: string) => {
    const colors = [
      "from-blue-400 to-blue-600",
      "from-purple-400 to-purple-600",
      "from-pink-400 to-pink-600",
      "from-green-400 to-green-600",
      "from-yellow-400 to-yellow-600",
      "from-red-400 to-red-600",
      "from-indigo-400 to-indigo-600",
      "from-teal-400 to-teal-600",
    ];
    const index = username.charCodeAt(0) % colors.length;
    return colors[index];
  };

  const getInitials = (username: string) => {
    return username.slice(0, 2).toUpperCase();
  };

  return (
    <div className="h-screen flex flex-col bg-gray-50">
      {/* Top Header */}
      <header className="bg-white border-b border-gray-200 px-6 py-4 flex-shrink-0 z-10">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-11 h-11 bg-gradient-to-br from-gray-500 to-red-600 rounded-2xl flex items-center justify-center">
              <img
                src="/logoIAO.jpg"
                alt="Logo"
                className="w-full h-full object-cover"
              />
            </div>
            <div>
              {/* capitaliser la 1er lettre de h1 */}
              <h1 className="text-xl font-bold text-gray-800 capitalize">
                {user.username}
              </h1>

              <p className="text-sm text-gray-500">Messagerie</p>
            </div>
          </div>

          {/* MODIFIÉ : Ajout du sélecteur AI */}
          <div className="flex items-center gap-3">
            <AIModelSelector
              models={aimodels}
              selectedModelId={selectedAIModelId}
              onSelectModel={setSelectedAIModelId}
              loading={loadingAIModels}
            />

            <button
              onClick={() => setShowLogoutModal(true)}
              className="flex items-center gap-2 px-4 py-2 text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-lg transition"
            >
              <LogOut className="w-5 h-5" />
              <span className="hidden sm:inline">Déconnexion</span>
            </button>
          </div>
        </div>
      </header>

      {/* Logout Modal */}
      <LogoutModal
        isOpen={showLogoutModal}
        onClose={() => setShowLogoutModal(false)}
        onConfirm={handleLogout}
        username={user.username}
      />

      {/* Main Content Area */}
      <div className="flex-1 flex overflow-hidden">
        {/* Users Sidebar */}
        <UsersSidebar
          users={users}
          currentUser={user}
          selectedUserId={selectedUserId}
          onSelectUser={setSelectedUserId}
          loading={loadingUsers}
        />

        {/* Chat Area */}
        <div className="flex-1 flex flex-col">
          {selectedUserId && selectedUser ? (
            <>
              {/* Chat Header */}
              <div className="bg-white border-b border-gray-200 px-6 py-4 flex items-center gap-3">
                <div
                  className={`w-10 h-10 rounded-full bg-gradient-to-br ${getAvatarColor(
                    selectedUser.username
                  )} flex items-center justify-center`}
                >
                  <span className="text-white font-semibold text-sm">
                    {getInitials(selectedUser.username)}
                  </span>
                </div>
                <div>
                  <h2 className="text-lg font-semibold text-gray-800 capitalize">
                    {selectedUser.username}
                  </h2>
                  <p className="text-sm text-green-500">En ligne</p>
                </div>
              </div>

              {/* Messages Container */}
              <div className="flex-1 overflow-y-auto bg-gray-50 p-6">
                {loadingMessages ? (
                  <div className="flex items-center justify-center h-full">
                    <Loader2 className="w-8 h-8 text-blue-500 animate-spin" />
                  </div>
                ) : messages.length === 0 ? (
                  <div className="flex items-center justify-center h-full">
                    <div className="text-center">
                      <MessageCircle className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                      <p className="text-gray-500">Aucun message</p>
                      <p className="text-sm text-gray-400">
                        Envoyez le premier message !
                      </p>
                    </div>
                  </div>
                ) : (
                  <div className="space-y-4 max-w-4xl mx-auto">
                    {messages.map((msg) => {
                      const isOwn = msg.senderUserId == user.idUser;
                      return (
                        <div
                          key={msg.idChat}
                          className={`flex ${
                            isOwn ? "justify-end" : "justify-start"
                          }`}
                        >
                          <div
                            className={`max-w-md px-4 py-2 rounded-2xl ${
                              isOwn
                                ? "bg-gradient-to-r from-blue-500 to-blue-600 text-white"
                                : "bg-gradient-to-r from-gray-200 to-gray-100 border border-gray-200 text-gray-800"
                            }`}
                          >
                            <p className="break-words">{msg.chatcontent}</p>
                            <p
                              className={`text-xs mt-1 ${
                                isOwn ? "text-blue-100" : "text-gray-500"
                              }`}
                            >
                              {msg.chatmaj && "Modifié • "}
                              {new Date(msg.created_at).toLocaleTimeString(
                                "fr-FR",
                                {
                                  hour: "2-digit",
                                  minute: "2-digit",
                                }
                              )}
                            </p>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>

              {/* Input Area */}
              <div className="bg-white border-t border-gray-200 p-4">
                <div className="max-w-4xl mx-auto flex gap-3">
                  <input
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    onKeyPress={handleKeyPress}
                    placeholder="Écrivez votre message..."
                    className="flex-1 px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                    disabled={sending}
                  />
                  <button
                    onClick={handleSend}
                    disabled={sending || !newMessage.trim()}
                    className="px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl font-semibold hover:shadow-lg transition disabled:opacity-70 disabled:cursor-not-allowed flex items-center gap-2"
                  >
                    {sending ? (
                      <Loader2 className="w-5 h-5 animate-spin" />
                    ) : (
                      <>
                        <Send className="w-5 h-5" />
                      </>
                    )}
                  </button>
                </div>
              </div>
            </>
          ) : (
            <div className="flex-1 flex items-center justify-center bg-gray-50">
              <div className="text-center">
                <MessageCircle className="w-20 h-20 text-gray-300 mx-auto mb-4" />
                <h3 className="text-xl font-semibold text-gray-600 mb-2">
                  Sélectionnez une conversation
                </h3>
                <p className="text-gray-400">
                  Choisissez un utilisateur dans la liste pour commencer à
                  discuter
                </p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
