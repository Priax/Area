"use client"

import { useState } from 'react';
import { useEffect } from 'react';

import {useTranslations} from 'next-intl';
import backgroundImage from '@/public/background.jpg'
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import Divider from '@mui/material/Divider';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormLabel from '@mui/material/FormLabel';
import FormControl from '@mui/material/FormControl';
import {Link} from '@/i18n/routing';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Card from '@mui/material/Card';
import GoogleIcon from '@mui/icons-material/Google';
import LocaleSwitcherAuth from '@/components/translation/LocaleSwitcherAuth';

import { setCookie } from 'cookies-next';
import { getCookie } from 'cookies-next';
import { useRouter } from 'next/navigation';

import LoginGoogleButton from '../../../components/googleSignIn/googleButton'

export default function Login() {
    const t = useTranslations('Login');

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const router = useRouter();

    useEffect(() => {
      const userToken = getCookie("userToken");
      const userId = getCookie("clientId");

      if (userToken !== undefined && userId !== undefined) {
          const currentLocale = getCookie("NEXT_LOCALE") || "en";          
          router.push(`/${currentLocale}`);
      }
  }, [router]);

    const sendData = async (event: any) => {
      try {
        const response = await fetch('http://localhost:8080/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            "email": email,
            "password": password
          }),
          cache: 'no-store'
        });
        
        if (!response.ok) {
          console.error("error", response)
          return;
        }

        const data = await response.json();

        if (getCookie("userToken") !== undefined) {
          console.error("userToken already set");
          return;
        }
        setCookie("userToken", data['right']);
        setCookie("clientId", data['left']);

        const currentLocale = getCookie("NEXT_LOCALE") || "en";
        router.push(`/${currentLocale}`);

      } catch (error) {
        console.error(error);
      }
    };

    return (
        <div style={{
            backgroundImage: `url(${backgroundImage.src})`,
            backgroundPosition: 'center',
            backgroundSize: 'cover'
        }}>
          <Stack
            sx={{
              justifyContent: 'center',
              height: '100dvh',
              p: 0,
            }}
          >
            <div className='flex justify-center'>
            <Card className='p-10 mx-5 lg:mx-0 w-full lg:w-1/2 bg-white bg-opacity-70 rounded-3xl backdrop-blur-md' variant="outlined">
              <Stack direction="row" className='mb-5'
                sx={{
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
              <Typography
                className='font-extrabold text-center'
                component="h1"
                variant="h4"
                sx={{ width: 'auto', fontSize: 'clamp(2rem, 10vw, 2.15rem)', color: "#f6466b" }}
              >
                {t('title')}
              </Typography>
              <LocaleSwitcherAuth/>
              </Stack>
              <Box
                component="form"
                sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}
                onSubmit={(event) => {
                  event.preventDefault();
                  sendData(event)
                }}
              >
                <FormControl>
                  <TextField
                    required
                    fullWidth
                    id="email"
                    placeholder={t('your_email')}
                    label="Email"
                    name="email"
                    autoComplete="email"
                    variant="outlined"
                    autoFocus
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </FormControl>
                <FormControl>
                  <TextField
                    required
                    fullWidth
                    name="password"
                    placeholder={t('your_password')}
                    label={t('password')}
                    type="password"
                    id="password"
                    autoComplete="password"
                    variant="outlined"
                    onChange={(e) => setPassword(e.target.value)}
                  />
                </FormControl>
                <FormControlLabel
                  control={<Checkbox value="rememberMe" color="primary" />}
                  label={t('remember_me')}
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                >
                    {t('title')}
                </Button>
                <Typography sx={{ textAlign: 'center' }}>
                    {t('not_register')}?{' '}
                  <span>
                    <Link
                      className='text-blue-600 visited:text-purple-600'
                      href="/signup"
                    //   style={{color: "#2979ff"}}
                    >
                      {t('signup')}
                    </Link>
                  </span>
                </Typography>
              </Box>
              <Divider className='my-3'>
                <Typography sx={{ color: 'text.secondary' }}>
                    {t('or')}
                </Typography>
              </Divider>
                <LoginGoogleButton/>
            </Card>
            </div>
          </Stack>
        </div>
    );
}

