import { useEffect } from 'react';
import { Loader2 } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';  
import { useNavigate } from 'react-router-dom';

export const LoadindPage = () => { 
  const { loading, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  // Redirige si déjà authentifié
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/messages');
    }
  }, [isAuthenticated, navigate]);
 
 

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <div className="flex flex-col items-center">
        <Loader2 className="w-12 h-12 text-blue-600 animate-spin mb-4" />
        <p className="text-gray-700 font-medium">Connexion en cours...</p>
      </div>
    </div>
  );
};