import React, { createContext, useState, useContext } from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import { theme } from '../styles/theme';

// Import de Ecrãs
import LoginScreen from '../screens/auth/LoginScreen';
import RegisterScreen from '../screens/auth/RegisterScreen';
import HomeScreen from '../screens/dashboard/HomeScreen';
import GroupDetailScreen from '../screens/group/GroupDetailScreen';
import SplashScreen from '../screens/splash/SplashScreen';
import OnboardingScreen from '../screens/onboarding/OnboardingScreen';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

// Import de Tipos
import { AuthStackParamList, TabParamList, MainStackParamList } from './types';

// Contexto de Autenticação para controlar fluxo logado/deslogado
interface AuthContextType {
  isAuthenticated: boolean;
  login: () => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  login: () => {},
  logout: () => {},
});

export const useAuth = () => useContext(AuthContext);

const AuthStack = createStackNavigator<AuthStackParamList>();
const Tab = createBottomTabNavigator<TabParamList>();
const MainStack = createStackNavigator<MainStackParamList>();

// --- Tipos de ecrã de entrada ---
type AppFlow = 'splash' | 'onboarding' | 'auth' | 'main';

// --- Fluxo de Autenticação (Deslogado) ---
function AuthNavigator() {
  return (
    <AuthStack.Navigator
      screenOptions={{
        headerShown: false,
        cardStyle: { backgroundColor: theme.colors.background },
      }}
    >
      <AuthStack.Screen name="Login" component={LoginScreen} />
      <AuthStack.Screen name="Register" component={RegisterScreen} />
    </AuthStack.Navigator>
  );
}

// --- Placeholder para Ecrã de Perfil ---
function ProfileScreen() {
  const { logout } = useAuth();
  return (
    <View style={styles.placeholderContainer}>
      <Ionicons name="person-circle-outline" size={80} color={theme.colors.primary} />
      <Text style={styles.placeholderTitle}>O meu Perfil</Text>
      <Text style={styles.placeholderText}>Funcionalidades de conta e definições de perfil.</Text>
      
      <TouchableOpacity style={styles.logoutButton} onPress={logout}>
        <Ionicons name="log-out-outline" size={20} color={theme.colors.text} style={{ marginRight: 8 }} />
        <Text style={styles.logoutButtonText}>Terminar Sessão</Text>
      </TouchableOpacity>
    </View>
  );
}

// --- Menu Tab Inferior ---
function TabNavigator() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName: keyof typeof Ionicons.glyphMap = 'help-circle-outline';

          if (route.name === 'Home') {
            iconName = focused ? 'wallet' : 'wallet-outline';
          } else if (route.name === 'Profile') {
            iconName = focused ? 'person' : 'person-outline';
          }

          return <Ionicons name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: theme.colors.primary,
        tabBarInactiveTintColor: theme.colors.textMuted,
        tabBarStyle: {
          backgroundColor: theme.colors.surface,
          borderTopColor: theme.colors.border,
          borderTopWidth: 1,
          height: 64,
          paddingBottom: 8,
          paddingTop: 8,
        },
        headerStyle: {
          backgroundColor: theme.colors.background,
          borderBottomColor: theme.colors.border,
          borderBottomWidth: 1,
          elevation: 0,
          shadowOpacity: 0,
        },
        headerTitleStyle: {
          color: theme.colors.text,
          fontSize: theme.typography.sizes.lg,
          fontWeight: 'bold',
        },
      })}
    >
      <Tab.Screen 
        name="Home" 
        component={HomeScreen} 
        options={{ title: 'Os meus Grupos' }}
      />
      <Tab.Screen 
        name="Profile" 
        component={ProfileScreen} 
        options={{ title: 'Perfil' }}
      />
    </Tab.Navigator>
  );
}

// --- Fluxo Principal de Telas (Logado) ---
function MainNavigator() {
  return (
    <MainStack.Navigator
      screenOptions={{
        headerStyle: {
          backgroundColor: theme.colors.background,
          elevation: 0,
          shadowOpacity: 0,
          borderBottomWidth: 1,
          borderBottomColor: theme.colors.border,
        },
        headerTitleStyle: {
          color: theme.colors.text,
          fontSize: theme.typography.sizes.lg,
          fontWeight: 'bold',
        },
        headerTintColor: theme.colors.primary,
        cardStyle: { backgroundColor: theme.colors.background },
      }}
    >
      <MainStack.Screen
        name="MainTabs"
        component={TabNavigator}
        options={{ headerShown: false }}
      />
      <MainStack.Screen
        name="GroupDetail"
        component={GroupDetailScreen}
        options={({ route }) => ({
          title: route.params.groupName,
          headerBackTitleVisible: false,
        })}
      />
    </MainStack.Navigator>
  );
}

// --- Provedor Principal de Navegação ---
export default function AppNavigator() {
  // Fluxo de navegação: splash → onboarding → auth → main
  const [flow, setFlow] = useState<AppFlow>('splash');
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const login = () => {
    setIsAuthenticated(true);
    setFlow('main');
  };
  const logout = () => {
    setIsAuthenticated(false);
    setFlow('auth');
  };

  const handleSplashFinish = () => setFlow('onboarding');
  const handleOnboardingFinish = () => setFlow('auth');

  // Renderizar ecrã correto com base no fluxo
  if (flow === 'splash') {
    return <SplashScreen onFinish={handleSplashFinish} />;
  }

  if (flow === 'onboarding') {
    return <OnboardingScreen onFinish={handleOnboardingFinish} />;
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
      {isAuthenticated || flow === 'main' ? <MainNavigator /> : <AuthNavigator />}
    </AuthContext.Provider>
  );
}

const styles = StyleSheet.create({
  placeholderContainer: {
    flex: 1,
    backgroundColor: theme.colors.background,
    justifyContent: 'center',
    alignItems: 'center',
    padding: theme.spacing.lg,
  },
  placeholderTitle: {
    fontSize: theme.typography.sizes.xl,
    fontWeight: 'bold',
    color: theme.colors.text,
    marginTop: theme.spacing.md,
    marginBottom: theme.spacing.sm,
  },
  placeholderText: {
    fontSize: theme.typography.sizes.md,
    color: theme.colors.textMuted,
    textAlign: 'center',
    marginBottom: theme.spacing.xl,
  },
  logoutButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.danger,
    paddingVertical: theme.spacing.sm + 4,
    paddingHorizontal: theme.spacing.lg,
    borderRadius: theme.borderRadius.md,
    ...theme.shadows.sm,
  },
  logoutButtonText: {
    color: theme.colors.text,
    fontSize: theme.typography.sizes.md,
    fontWeight: '600',
  },
});
