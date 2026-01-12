import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import { LoadindPage } from './pages/LoadindPage';
import { MessagesPage } from './pages/MessagesPage';
import type { JSX } from 'react';

const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) return < LoadindPage />;
  if (!isAuthenticated) return <Navigate to="/" replace />;

  return children;
};

const PublicRoute = ({ children }: { children: JSX.Element }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) return < LoadindPage />;
  if (isAuthenticated) return <Navigate to="/messages" replace />;

  return children;
};

export default function App() {
  const { user, logout } = useAuth();
  return (
    <Routes>
      <Route
        path="/"
        element={
          <PublicRoute>
            <LoadindPage />
          </PublicRoute>
        }
      />

      <Route
        path="/messages"
        element={
        <ProtectedRoute>
          {user ? <MessagesPage user={user} onLogout={logout} /> : < LoadindPage />}
        </ProtectedRoute>
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
