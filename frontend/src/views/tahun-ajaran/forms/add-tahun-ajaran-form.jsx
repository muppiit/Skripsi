/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import { Form, Input, Modal, Select, Row, Col, message } from "antd";
import { getStudyPrograms } from "@/api/studyProgram";

const { Option } = Select;

const AddTahunAjaranForm = ({ visible, onCancel, onOk, confirmLoading }) => {
  const [form] = Form.useForm();
  const [studyProgramList, setStudyProgramList] = useState([]);
  const [loadingPrograms, setLoadingPrograms] = useState(false);

  const fetchStudyProgramList = async () => {
    setLoadingPrograms(true);
    try {
      const result = await getStudyPrograms();
      if (result.data.statusCode === 200) {
        setStudyProgramList(result.data.content);
      } else {
        message.error("Gagal mengambil data program studi");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    } finally {
      setLoadingPrograms(false);
    }
  };

  useEffect(() => {
    if (visible) {
      fetchStudyProgramList();
      form.resetFields();
    }
  }, [visible]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (error) {
      message.error("Harap lengkapi semua field yang wajib diisi");
    }
  };

  return (
    <Modal
      title="Tambah Tahun Ajaran"
      open={visible}
      onCancel={() => {
        onCancel();
        form.resetFields();
      }}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
      width={500}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Program Studi:"
              name="idStudyProgram"
              rules={[{ required: true, message: "Silahkan pilih Program Studi" }]}
            >
              <Select
                placeholder="Pilih Program Studi"
                loading={loadingPrograms}
                showSearch
                optionFilterProp="children"
              >
                {studyProgramList.map((item) => (
                  <Option key={item.id} value={item.id}>
                    {item.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Tahun Ajaran:"
              name="tahunAjaran"
              rules={[
                { required: true, message: "Silahkan isikan Tahun Ajaran" },
              ]}
            >
              <Input placeholder="Contoh: 2024/2025" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default AddTahunAjaranForm;
