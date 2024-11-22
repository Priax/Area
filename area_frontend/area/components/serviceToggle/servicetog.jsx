import React, { useState } from 'react';
import Box from '@mui/material/Box';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';

export default function Servicetog({ serviceName }) {
    const [loading, setLoading] = useState(true);

    return (
        <FormControlLabel
            sx={{
                alignItems: 'center',
                pl: 6,
            }}
            control={
                <Switch
                    checked={loading}
                    onChange={() => setLoading(!loading)}
                    name="loading"
                    color="primary"
                />
            }
            label={
                <Box display="flex" alignItems="center" gap={1} sx={{ pointerEvents: 'none' }}>
                    {serviceName[0].toUpperCase() + serviceName.slice(1).toLowerCase()}
                    <img
                        src={`/${serviceName.toLowerCase()}.png`}
                        style={{ width: '50px', height: '50px', marginLeft: '40px' }}
                        alt={`${serviceName} logo`}
                    />
                </Box>
            }
            labelPlacement="end"
        />
    );
}
