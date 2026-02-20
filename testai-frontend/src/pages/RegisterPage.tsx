import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Input from "../components/common/Input";
import Button from "../components/common/Button";
import Card from "../components/common/Card";
import {
  EnvelopeIcon,
  LockClosedIcon,
  UserIcon,
  ArrowLeftIcon,
  CheckCircleIcon,
  PhoneIcon,
  BuildingOfficeIcon,
} from "@heroicons/react/24/outline";
import authService from "../services/authService";

const RegisterPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    phoneNumber: "",
    company: "",
  });
  const [errors, setErrors] = useState<any>({});
  const [serverError, setServerError] = useState("");
  const navigate = useNavigate();

  const validate = () => {
    const newErrors: any = {};

    // Nom
    if (formData.name.length < 2) {
      newErrors.name = "Le nom doit faire au moins 2 caractères";
    }

    // Email
    if (!formData.email.includes("@")) {
      newErrors.email = "Email invalide";
    }

    // Mot de passe
    if (formData.password.length < 8) {
      newErrors.password = "Le mot de passe doit faire au moins 8 caractères";
    }

    // Confirmation mot de passe
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Les mots de passe ne correspondent pas";
    }

    // Téléphone (optionnel mais si rempli, doit être valide)
    if (formData.phoneNumber && formData.phoneNumber.length < 10) {
      newErrors.phoneNumber = "Numéro de téléphone invalide";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    setServerError("");

    try {
      const response = await authService.register({
        name: formData.name,
        email: formData.email,
        password: formData.password,
        phoneNumber: formData.phoneNumber || undefined,
        company: formData.company || undefined,
        role: "MANAGER",
      });

      // ⭐️ Rediriger vers la page d'attente (pas verify-email directement)
      navigate("/verification-pending", {
        state: {
          email: formData.email,
          message: response.message,
        },
      });
    } catch (error: any) {
      console.error("Erreur inscription:", error);
      setServerError(
        error.response?.data?.message ||
          "Une erreur est survenue lors de l'inscription. Veuillez réessayer.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex flex-col md:flex-row items-center justify-center p-4 gap-12">
      {/* Section Avantages */}
      <div className="hidden lg:block max-w-md">
        <h2 className="text-4xl font-bold text-gray-900 mb-8 leading-tight">
          Pourquoi rejoindre <span className="text-primary">TestAI</span> ?
        </h2>
        <ul className="space-y-6">
          <li className="flex gap-4">
            <CheckCircleIcon className="w-8 h-8 text-primary shrink-0" />
            <div>
              <p className="font-bold">Gain de temps massif</p>
              <p className="text-gray-600 text-sm">
                Économisez des heures de rédaction manuelle de tests.
              </p>
            </div>
          </li>
          <li className="flex gap-4">
            <CheckCircleIcon className="w-8 h-8 text-primary shrink-0" />
            <div>
              <p className="font-bold">Zéro oubli</p>
              <p className="text-gray-600 text-sm">
                L'IA teste tous les scénarios, même les plus improbables.
              </p>
            </div>
          </li>
          <li className="flex gap-4">
            <CheckCircleIcon className="w-8 h-8 text-primary shrink-0" />
            <div>
              <p className="font-bold">Prêt pour l'entreprise</p>
              <p className="text-gray-600 text-sm">
                Intégration Jenkins et rapports conformes ISO.
              </p>
            </div>
          </li>
        </ul>
      </div>

      {/* Formulaire d'inscription */}
      <div className="w-full max-w-lg">
        <Link
          to="/"
          className="mb-8 flex items-center text-gray-500 hover:text-primary transition lg:hidden"
        >
          <ArrowLeftIcon className="w-4 h-4 mr-2" />
          Retour
        </Link>

        <Card className="shadow-2xl">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900">
              Créer un compte
            </h1>
            <p className="text-gray-500 mt-2">
              Démarrer votre essai gratuit de 14 jours.
            </p>
          </div>

          {/* Erreur serveur */}
          {serverError && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-red-600 text-sm">{serverError}</p>
            </div>
          )}

          <form onSubmit={handleRegister} className="space-y-4">
            {/* Nom */}
            <Input
              label="Nom complet"
              placeholder="Jean Dupont"
              value={formData.name}
              onChange={(e) =>
                setFormData({ ...formData, name: e.target.value })
              }
              error={errors.name}
              icon={<UserIcon className="h-5 w-5" />}
              required
            />

            {/* Email */}
            <Input
              label="Email professionnel"
              type="email"
              placeholder="jean@entreprise.com"
              value={formData.email}
              onChange={(e) =>
                setFormData({ ...formData, email: e.target.value })
              }
              error={errors.email}
              icon={<EnvelopeIcon className="h-5 w-5" />}
              required
            />

            {/* Téléphone (optionnel) */}
            <Input
              label="Téléphone (optionnel)"
              type="tel"
              placeholder="+33612345678"
              value={formData.phoneNumber}
              onChange={(e) =>
                setFormData({ ...formData, phoneNumber: e.target.value })
              }
              error={errors.phoneNumber}
              icon={<PhoneIcon className="h-5 w-5" />}
            />

            {/* Entreprise (optionnel) */}
            <Input
              label="Entreprise (optionnel)"
              placeholder="Mon Entreprise"
              value={formData.company}
              onChange={(e) =>
                setFormData({ ...formData, company: e.target.value })
              }
              icon={<BuildingOfficeIcon className="h-5 w-5" />}
            />

            {/* Mots de passe */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Mot de passe"
                type="password"
                placeholder="••••••••"
                value={formData.password}
                onChange={(e) =>
                  setFormData({ ...formData, password: e.target.value })
                }
                error={errors.password}
                icon={<LockClosedIcon className="h-5 w-5" />}
                required
              />
              <Input
                label="Confirmer"
                type="password"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={(e) =>
                  setFormData({ ...formData, confirmPassword: e.target.value })
                }
                error={errors.confirmPassword}
                icon={<LockClosedIcon className="h-5 w-5" />}
                required
              />
            </div>

            {/* Conditions */}
            <label className="flex items-start gap-2 cursor-pointer mb-6">
              <input
                type="checkbox"
                required
                className="mt-1 w-4 h-4 text-primary rounded border-gray-300 focus:ring-primary"
              />
              <span className="text-sm text-gray-600">
                J'accepte les{" "}
                <a href="#" className="text-primary underline">
                  conditions d'utilisation
                </a>{" "}
                et la politique de confidentialité.
              </span>
            </label>

            {/* Bouton */}
            <Button type="submit" className="w-full" loading={loading}>
              Créer mon compte
            </Button>
          </form>

          {/* Lien connexion */}
          <p className="mt-8 text-center text-gray-600">
            Déjà inscrit ?{" "}
            <Link
              to="/login"
              className="font-bold text-primary hover:underline"
            >
              Connectez-vous
            </Link>
          </p>
        </Card>
      </div>
    </div>
  );
};

export default RegisterPage;
