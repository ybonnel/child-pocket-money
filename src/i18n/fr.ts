/**
 * French strings — mirrors the Android strings.xml (FR source).
 */
export const fr = {
  // App
  app_name: "Argent de poche",

  // ChildList screen
  screen_children_title: "Mes enfants",
  children_empty_title: "Aucun enfant",
  children_empty_subtitle: "Appuyez sur + pour ajouter un enfant",
  btn_add_child: "Ajouter un enfant",

  // ChildDetail screen
  screen_child_detail_title: (name: string) => name,
  btn_add_credit: "Ajouter",
  btn_add_debit: "Retirer",
  btn_edit_child: "Modifier",
  transactions_empty_title: "Aucune transaction",
  transactions_empty_subtitle: "Les transactions apparaîtront ici",
  label_balance: "Solde",
  label_weekly_allowance: "Argent de poche",
  label_allowance_day: "Versement le",
  confirm_delete_transaction: "Supprimer cette transaction ?",
  btn_delete: "Supprimer",
  btn_cancel: "Annuler",

  // ChildEdit screen
  screen_add_child_title: "Nouvel enfant",
  screen_edit_child_title: "Modifier",
  label_child_name: "Prénom",
  label_child_color: "Couleur",
  label_weekly_amount: "Montant hebdomadaire",
  label_allowance_day_of_week: "Jour de versement",
  label_allowance_active: "Versement actif",
  btn_save: "Enregistrer",
  error_name_required: "Le prénom est requis",
  error_invalid_amount: "Montant invalide",
  confirm_delete_child: "Supprimer cet enfant et toutes ses transactions ?",

  // TransactionEdit screen
  screen_add_credit_title: "Ajouter de l\u2019argent",
  screen_add_debit_title: "Retirer de l\u2019argent",
  label_amount: "Montant",
  label_reason: "Motif",
  placeholder_reason: "Ex\u00a0: cadeau, \u00e9conomies\u2026",
  error_amount_required: "Le montant est requis",

  // Settings screen
  screen_settings_title: "Param\u00e8tres",
  label_currency: "Devise",
  label_theme: "Th\u00e8me",
  theme_system: "Syst\u00e8me",
  theme_light: "Clair",
  theme_dark: "Sombre",
  btn_install_app: "Installer l\u2019application",
  label_about: "\u00c0 propos",
  about_version: "Version 1.0",
  about_description: "Application 100\u00a0% locale \u2014 aucune donn\u00e9e envoy\u00e9e en ligne.",

  // Transaction types
  transaction_type_allowance: "Argent de poche",
  transaction_type_credit: "Cr\u00e9dit",
  transaction_type_debit: "D\u00e9bit",
  transaction_type_adjustment: "Ajustement",

  // Weekdays
  weekday_1: "Lundi",
  weekday_2: "Mardi",
  weekday_3: "Mercredi",
  weekday_4: "Jeudi",
  weekday_5: "Vendredi",
  weekday_6: "Samedi",
  weekday_7: "Dimanche",
} as const;

export type Strings = typeof fr;
