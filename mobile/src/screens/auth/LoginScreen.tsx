import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  SafeAreaView,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { theme } from '../../styles/theme';
import { useAuth } from '../../navigation/AppNavigator';
import { AuthService } from '../../services/api';
import { StackScreenProps } from '@react-navigation/stack';
import { AuthStackParamList } from '../../navigation/types';

type Props = StackScreenProps<AuthStackParamList, 'Login'>;

export default function LoginScreen({ navigation }: Props) {
  const { login: setAuthenticated } = useAuth();
  
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  
  const [emailFocused, setEmailFocused] = useState(false);
  const [passwordFocused, setPasswordFocused] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleLogin = async () => {
    if (!email || !password) {
      setErrorMessage('Por favor, preencha todos os campos.');
      return;
    }

    setLoading(true);
    setErrorMessage(null);

    try {
      // Chamada de serviço da API para efetuar o login (preparado para integrar com o Java REST)
      const response = await AuthService.login(email, password);
      console.log('Login efetuado com sucesso:', response.user.name);
      
      // Define o estado de autenticado no contexto global
      setAuthenticated();
    } catch (error: any) {
      setErrorMessage(error.message || 'Erro ao efetuar login.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.container}
      >
        <ScrollView contentContainerStyle={styles.scrollContent} showsVerticalScrollIndicator={false}>
          
          {/* Logo e Cabeçalho */}
          <View style={styles.headerContainer}>
            <View style={styles.logoBadge}>
              <Ionicons name="pie-chart" size={36} color={theme.colors.primary} />
            </View>
            <Text style={styles.appTitle}>Apportion</Text>
            <Text style={styles.subtitle}>Gere e divida as suas despesas sem complicações.</Text>
          </View>

          {/* Mensagem de Erro */}
          {errorMessage && (
            <View style={styles.errorCard}>
              <Ionicons name="alert-circle-outline" size={20} color={theme.colors.danger} />
              <Text style={styles.errorText}>{errorMessage}</Text>
            </View>
          )}

          {/* Formulário */}
          <View style={styles.formContainer}>
            <Text style={styles.inputLabel}>Endereço de E-mail</Text>
            <View
              style={[
                styles.inputWrapper,
                emailFocused && styles.inputWrapperFocused,
              ]}
            >
              <Ionicons
                name="mail-outline"
                size={20}
                color={emailFocused ? theme.colors.primary : theme.colors.textMuted}
                style={styles.inputIcon}
              />
              <TextInput
                style={styles.input}
                placeholder="exemplo@email.com"
                placeholderTextColor={theme.colors.textMuted}
                value={email}
                onChangeText={setEmail}
                onFocus={() => setEmailFocused(true)}
                onBlur={() => setEmailFocused(false)}
                keyboardType="email-address"
                autoCapitalize="none"
                autoCorrect={false}
              />
            </View>

            <Text style={[styles.inputLabel, { marginTop: theme.spacing.md }]}>Palavra-passe</Text>
            <View
              style={[
                styles.inputWrapper,
                passwordFocused && styles.inputWrapperFocused,
              ]}
            >
              <Ionicons
                name="lock-closed-outline"
                size={20}
                color={passwordFocused ? theme.colors.primary : theme.colors.textMuted}
                style={styles.inputIcon}
              />
              <TextInput
                style={styles.input}
                placeholder="Insira a sua palavra-passe"
                placeholderTextColor={theme.colors.textMuted}
                value={password}
                onChangeText={setPassword}
                onFocus={() => setPasswordFocused(true)}
                onBlur={() => setPasswordFocused(false)}
                secureTextEntry={!showPassword}
                autoCapitalize="none"
                autoCorrect={false}
              />
              <TouchableOpacity onPress={() => setShowPassword(!showPassword)} style={styles.togglePassword}>
                <Ionicons
                  name={showPassword ? 'eye-off-outline' : 'eye-outline'}
                  size={20}
                  color={theme.colors.textMuted}
                />
              </TouchableOpacity>
            </View>

            {/* Esqueceu-se da Password */}
            <TouchableOpacity style={styles.forgotPasswordButton}>
              <Text style={styles.forgotPasswordText}>Esqueceu-se da palavra-passe?</Text>
            </TouchableOpacity>

            {/* Botão Entrar */}
            <TouchableOpacity
              style={styles.loginButton}
              onPress={handleLogin}
              disabled={loading}
              activeOpacity={0.8}
            >
              {loading ? (
                <ActivityIndicator color={theme.colors.white} />
              ) : (
                <Text style={styles.loginButtonText}>Entrar</Text>
              )}
            </TouchableOpacity>
          </View>

          {/* Separador de Login Social */}
          <View style={styles.dividerContainer}>
            <View style={styles.dividerLine} />
            <Text style={styles.dividerText}>ou continuar com</Text>
            <View style={styles.dividerLine} />
          </View>

          {/* Botões Sociais */}
          <View style={styles.socialButtonsContainer}>
            <TouchableOpacity style={styles.socialButton} activeOpacity={0.8}>
              <Ionicons name="logo-google" size={20} color={theme.colors.google} style={{ marginRight: 8 }} />
              <Text style={styles.socialButtonText}>Google</Text>
            </TouchableOpacity>

            <TouchableOpacity style={[styles.socialButton, styles.appleButton]} activeOpacity={0.8}>
              <Ionicons name="logo-apple" size={20} color={theme.colors.surface} style={{ marginRight: 8 }} />
              <Text style={[styles.socialButtonText, styles.appleButtonText]}>Apple</Text>
            </TouchableOpacity>
          </View>

          {/* Registo / Criar Conta */}
          <View style={styles.footerContainer}>
            <Text style={styles.footerText}>Não tem uma conta? </Text>
            <TouchableOpacity onPress={() => navigation.navigate('Register')}>
              <Text style={styles.footerLink}>Registe-se</Text>
            </TouchableOpacity>
          </View>

        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: theme.colors.background,
  },
  container: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    paddingHorizontal: theme.spacing.lg,
    paddingTop: theme.spacing.xl,
    paddingBottom: theme.spacing.xl,
    justifyContent: 'center',
  },
  headerContainer: {
    alignItems: 'center',
    marginBottom: theme.spacing.xl + 8,
  },
  logoBadge: {
    backgroundColor: theme.colors.surface,
    padding: theme.spacing.md,
    borderRadius: theme.borderRadius.lg,
    borderWidth: 1,
    borderColor: theme.colors.border,
    marginBottom: theme.spacing.md,
    ...theme.shadows.md,
  },
  appTitle: {
    fontSize: theme.typography.sizes.title,
    fontWeight: 'bold',
    color: theme.colors.text,
    letterSpacing: 0.5,
  },
  subtitle: {
    fontSize: theme.typography.sizes.sm + 1,
    color: theme.colors.textMuted,
    textAlign: 'center',
    marginTop: theme.spacing.xs,
    paddingHorizontal: theme.spacing.md,
  },
  errorCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(244, 63, 94, 0.1)',
    borderWidth: 1,
    borderColor: theme.colors.danger,
    borderRadius: theme.borderRadius.md,
    padding: theme.spacing.md,
    marginBottom: theme.spacing.lg,
  },
  errorText: {
    color: theme.colors.danger,
    fontSize: theme.typography.sizes.sm,
    fontWeight: '500',
    marginLeft: theme.spacing.sm,
    flex: 1,
  },
  formContainer: {
    marginBottom: theme.spacing.xl,
  },
  inputLabel: {
    fontSize: theme.typography.sizes.sm,
    fontWeight: '600',
    color: theme.colors.text,
    marginBottom: theme.spacing.xs,
    marginLeft: 2,
  },
  inputWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.borderRadius.md,
    paddingHorizontal: theme.spacing.md,
    height: 54,
  },
  inputWrapperFocused: {
    borderColor: theme.colors.primary,
    borderWidth: 1.5,
  },
  inputIcon: {
    marginRight: theme.spacing.sm,
  },
  input: {
    flex: 1,
    color: theme.colors.text,
    fontSize: theme.typography.sizes.md,
    height: '100%',
  },
  togglePassword: {
    padding: theme.spacing.xs,
  },
  forgotPasswordButton: {
    alignSelf: 'flex-end',
    marginTop: theme.spacing.sm,
    marginBottom: theme.spacing.lg,
  },
  forgotPasswordText: {
    fontSize: theme.typography.sizes.sm,
    color: theme.colors.primaryLight,
    fontWeight: '500',
  },
  loginButton: {
    backgroundColor: theme.colors.primary,
    height: 54,
    borderRadius: theme.borderRadius.md,
    justifyContent: 'center',
    alignItems: 'center',
    ...theme.shadows.md,
  },
  loginButtonText: {
    color: theme.colors.white,
    fontSize: theme.typography.sizes.md,
    fontWeight: 'bold',
  },
  dividerContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: theme.spacing.lg,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: theme.colors.border,
  },
  dividerText: {
    marginHorizontal: theme.spacing.md,
    color: theme.colors.textMuted,
    fontSize: theme.typography.sizes.xs + 1,
  },
  socialButtonsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: theme.spacing.xl,
  },
  socialButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderColor: theme.colors.border,
    height: 50,
    borderRadius: theme.borderRadius.md,
    marginHorizontal: theme.spacing.xs,
    ...theme.shadows.sm,
  },
  appleButton: {
    backgroundColor: theme.colors.white,
  },
  appleButtonText: {
    color: theme.colors.background,
  },
  socialButtonText: {
    color: theme.colors.text,
    fontSize: theme.typography.sizes.sm + 1,
    fontWeight: '600',
  },
  footerContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: theme.spacing.sm,
  },
  footerText: {
    color: theme.colors.textMuted,
    fontSize: theme.typography.sizes.sm,
  },
  footerLink: {
    color: theme.colors.primaryLight,
    fontSize: theme.typography.sizes.sm,
    fontWeight: 'bold',
  },
});
