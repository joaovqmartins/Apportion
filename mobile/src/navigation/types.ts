import { RouteProp } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { BottomTabNavigationProp } from '@react-navigation/bottom-tabs';

// ParamList para o fluxo de autenticação
export type AuthStackParamList = {
  Login: undefined;
  Register: undefined;
};

// ParamList para o fluxo principal de tabs (logado)
export type TabParamList = {
  Home: undefined;
  Profile: undefined; // Placeholder para ecrã de perfil
};

// ParamList para o stack principal (logado)
export type MainStackParamList = {
  MainTabs: undefined;
  GroupDetail: {
    groupId: string;
    groupName: string;
  };
};

// --- Tipagem de Navegação Útil ---

// Navegação do AuthStack
export type AuthNavigationProp<T extends keyof AuthStackParamList> = StackNavigationProp<
  AuthStackParamList,
  T
>;

// Navegação do MainStack
export type MainNavigationProp<T extends keyof MainStackParamList> = StackNavigationProp<
  MainStackParamList,
  T
>;

// Navegação de Tabs
export type TabNavigationProp<T extends keyof TabParamList> = BottomTabNavigationProp<
  TabParamList,
  T
>;
