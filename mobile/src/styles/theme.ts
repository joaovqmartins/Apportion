export const theme = {
  colors: {
    background: '#0B0A0F', // Escuro profundo com tom violeta
    surface: '#151324',    // Cartão/superfície
    surfaceElevated: '#1D1A30', // Superfície sob foco
    primary: '#8B5CF6',    // Violeta vibrante
    primaryLight: '#A78BFA',
    secondary: '#EC4899',  // Rosa/Magenta vibrante
    success: '#10B981',    // Verde Esmeralda (Saldo positivo)
    danger: '#F43F5E',     // Vermelho Coral (O que deves)
    warning: '#F59E0B',    // Amber (O que te devem)
    text: '#F3F4F6',       // Branco suave
    textMuted: '#9CA3AF',  // Cinzento claro
    border: '#2A2744',     // Borda sutil
    shadow: '#000000',
    white: '#FFFFFF',
    transparent: 'transparent',
    google: '#EA4335',
    apple: '#FFFFFF',
  },
  spacing: {
    xs: 4,
    sm: 8,
    md: 16,
    lg: 24,
    xl: 32,
    xxl: 40,
  },
  borderRadius: {
    sm: 8,
    md: 14,
    lg: 20,
    xl: 30,
    full: 9999,
  },
  typography: {
    fontFamily: {
      regular: 'System',
      medium: 'System-Medium',
      bold: 'System-Bold',
    },
    sizes: {
      xs: 12,
      sm: 14,
      md: 16,
      lg: 18,
      xl: 20,
      xxl: 26,
      title: 34,
    },
  },
  shadows: {
    sm: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.1,
      shadowRadius: 4,
      elevation: 2,
    },
    md: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 6 },
      shadowOpacity: 0.25,
      shadowRadius: 10,
      elevation: 6,
    },
    lg: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 10 },
      shadowOpacity: 0.4,
      shadowRadius: 20,
      elevation: 12,
    },
  },
};
